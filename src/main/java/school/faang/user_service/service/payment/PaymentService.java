package school.faang.user_service.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment_service.PaymentRequest;
import school.faang.user_service.dto.payment_service.PaymentResponse;
import school.faang.user_service.entity.premium.PremiumPeriod;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentServiceClient paymentServiceClient;

    public PaymentResponse sendPaymentRequest(PremiumPeriod period) {
        PaymentRequest request = PaymentRequest.builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(period.getPrice())
                .currency(period.getCurrency())
                .build();

        log.info("Sending payment request with paymentNumber: {}", request.paymentNumber());
        return paymentServiceClient.sendPaymentRequest(request);
    }
}
