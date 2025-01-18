package school.faang.user_service.service.promotion;

import jakarta.transaction.Transactional;
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
        return promotionPaymentMapper.toDto(promotionPaymentRepository.findPromotionPaymentById(id));
    }

    @Override
    @Transactional
    public PromotionPaymentDto sendAndCreate(PromotionRequestDto dto) {
        PromotionPayment newPayment = createPayment(dto);
        PaymentRequest paymentRequest = createPaymentRequest(newPayment);
        PaymentResponse paymentResponse = promotionPaymentClient.sendPayment(paymentRequest);
        newPayment.setStatus(getPaymentStatus(paymentResponse));

        PromotionPayment savedPayment = promotionPaymentRepository.save(newPayment);
        return promotionPaymentMapper.toDto(savedPayment);
    }

    private PromotionPayment createPayment(PromotionRequestDto dto) {
        PromotionPayment promotionPayment = new PromotionPayment();
        promotionPayment.setId(UUID.randomUUID());
        promotionPayment.setUserId(dto.getUserId());
        promotionPayment.setAmount(dto.getAmount());
        promotionPayment.setCurrency(dto.getCurrency());
        return promotionPayment;
    }

    private static PaymentRequest createPaymentRequest(PromotionPayment newPayment) {
        return PaymentRequest.builder()
                .paymentNumber(newPayment.getId())
                .amount(newPayment.getAmount())
                .currency(newPayment.getCurrency())
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
