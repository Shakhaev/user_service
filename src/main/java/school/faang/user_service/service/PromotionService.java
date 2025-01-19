package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.promotion.PaymentStatus;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.Promotion.PromotionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final UserService userService;
    private final PromotionRepository promotionRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PromotionMapper promotionMapper;
    private BigDecimal costPerImpression;

    @Transactional
    public PromotionDto createPromotion(PromotionRequestDto requestDTO) {
        if (!userService.isUserExist(requestDTO.getUserId())) {
            throw new EntityNotFoundException("Пользователь не существует");
        }

        promotionPayment(requestDTO);
        Promotion promotion = promotionMapper.toEntity(requestDTO);
        promotionRepository.save(promotion);
        return promotionMapper.toDto(promotion);
    }

    public List<PromotionDto> getAllPromotionsForUser(Long userId) {
        List<Promotion> promotions = promotionRepository.findActivePromotionsByUserId(userId);
        return promotions.stream()
                .map(promotionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PromotionDto updatePromotion(PromotionRequestDto requestDTO, Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion не найден"));

        BigDecimal additionalCost = calculateAdditionalCost(promotion, requestDTO);

        if (additionalCost.compareTo(BigDecimal.ZERO) > 0) {
            PaymentResponseDto paymentResponseDto = paymentServiceClient.createOrder(requestDTO.getPaymentRequest(), promotion.getUserId());
            checkPaymentStatus(paymentResponseDto);
        }

        promotionMapper.updatePromotionFromDto(requestDTO, promotion);
        promotionRepository.save(promotion);
        return promotionMapper.toDto(promotion);
    }

    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion не найден"));

        promotion.setActive(false);
        promotionRepository.save(promotion);
    }

    private void promotionPayment(PromotionRequestDto requestDTO) {
        PaymentResponseDto response = paymentServiceClient.createOrder(
                requestDTO.getPaymentRequest(),
                requestDTO.getUserId());

        checkPaymentStatus(response);
    }

    private void checkPaymentStatus(PaymentResponseDto paymentResponseDto) {
        if (paymentResponseDto.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentFailedException("Оплата не прошла");
        }
    }

    private BigDecimal calculateAdditionalCost(Promotion promotion, PromotionRequestDto promotionRequestDto) {
        if (promotionRequestDto.getImpressionsLimit() > promotion.getImpressionsLimit()) {
            int additionalImpressions = promotionRequestDto.getImpressionsLimit() - promotion.getImpressionsLimit();
            return costPerImpression.multiply(BigDecimal.valueOf(additionalImpressions));
        }
        return BigDecimal.ZERO;
    }
}
