package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;

@FeignClient(name = "payment-service", url = "http://localhost:9080")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponse sendPayment(@RequestBody PaymentRequest paymentRequest);
}
