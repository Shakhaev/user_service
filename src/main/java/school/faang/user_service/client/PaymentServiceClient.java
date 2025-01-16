package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.res.PaymentResponse;

@FeignClient(name = "payment-service", url = "localhost:9080/api")
public interface PaymentServiceClient {

    @PostMapping("/payment")
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto);
}
