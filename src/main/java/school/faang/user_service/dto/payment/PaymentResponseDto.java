package school.faang.user_service.dto.payment;

import lombok.Data;
import school.faang.user_service.dto.promotion.PaymentStatus;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDto {
    private long id;
    private long userId;
    private String paymentMethod;
    private String paymentLink;
    private String servicePlan;
    private String serviceType;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
}