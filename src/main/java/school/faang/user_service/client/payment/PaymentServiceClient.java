package school.faang.user_service.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.payment_service.PaymentRequest;
import school.faang.user_service.dto.payment_service.PaymentResponse;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponse sendPaymentRequest(@RequestBody PaymentRequest paymentRequest);
}
