package school.faang.user_service.dto.paymentService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private long id;
    private long userId;
    private String paymentMethod;
    private String paymentLink;
    private String servicePlan;
    private String serviceType;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
}