package school.faang.user_service.client.payment;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://${payment-service.host}:${payment-service.port}")
public interface PaymentServiceFeignClient {
    @PostMapping(value = "/api/payment")
    PaymentResponse sendPayment(@RequestBody PaymentRequest paymentRequest);
}
