package school.faang.user_service.client.promotion;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.promotion.PaymentRequest;
import school.faang.user_service.dto.promotion.PaymentResponse;

@FeignClient(name = "promotionPayment", url = "http://${payment-service.host}:${payment-service.port}")
public interface PromotionPaymentFeignClient {
    @PostMapping(value = "/api/payment", consumes = MediaType.APPLICATION_JSON_VALUE)
    PaymentResponse sendPayment(@RequestBody PaymentRequest paymentRequest);
}
