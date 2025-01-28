package school.faang.user_service.service.promotion;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.promotion.PromotionPaymentFeignClient;
import school.faang.user_service.dto.promotion.PaymentRequest;
import school.faang.user_service.dto.promotion.PaymentResponse;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.entity.promotion.PromotionPayment;
import school.faang.user_service.enums.promotion.PaymentStatus;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;
import school.faang.user_service.mapper.promotion.PromotionPaymentMapper;
import school.faang.user_service.repository.promotion.PromotionPaymentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionPaymentServiceImpl implements PromotionPaymentService {
    private final PromotionPaymentRepository promotionPaymentRepository;
    private final PromotionPaymentMapper promotionPaymentMapper;
    private final PromotionPaymentFeignClient promotionPaymentClient;

    @Override
    public PromotionPaymentDto getPromotionPaymentById(UUID id) {
        PromotionPayment promotionPayment = promotionPaymentRepository.findPromotionPaymentById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Promotion payment with id = %s not found", id)));
        return promotionPaymentMapper.toDto(promotionPayment);
    }

    @Override
    public PromotionPaymentDto sendAndCreate(PromotionRequestDto dto) {
        PaymentRequest paymentRequest = buildPaymentRequest(dto);
        PaymentResponse paymentResponse = promotionPaymentClient.sendPayment(paymentRequest);
        PromotionPayment newPayment = buildPayment(dto);
        newPayment.setStatus(getPaymentStatus(paymentResponse));

        PromotionPayment savedPayment = promotionPaymentRepository.save(newPayment);
        return promotionPaymentMapper.toDto(savedPayment);
    }

    private PromotionPayment buildPayment(PromotionRequestDto dto) {
        PromotionPayment promotionPayment = new PromotionPayment();
        promotionPayment.setId(UUID.randomUUID());
        promotionPayment.setUserId(dto.getUserId());
        promotionPayment.setAmount(dto.getAmount());
        promotionPayment.setCurrency(dto.getCurrency());
        return promotionPayment;
    }

    private PaymentRequest buildPaymentRequest(PromotionRequestDto requestDto) {
        return PaymentRequest.builder()
                .paymentNumber(UUID.randomUUID())
                .amount(requestDto.getAmount())
                .currency(requestDto.getCurrency())
                .build();
    }

    private PromotionPaymentStatus getPaymentStatus(PaymentResponse paymentResponse) {
        PaymentStatus status = paymentResponse.status();
        if (PaymentStatus.SUCCESS.equals(status)) {
            return PromotionPaymentStatus.ACCEPTED;
        } else {
            return PromotionPaymentStatus.DECLINED;
        }
    }
}
