package school.faang.user_service.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;

@FeignClient(name = "payment-service", url = "${payment-service.url}")
public interface PaymentServiceClient {

    PaymentResponseDto createOrder(
            @RequestBody @Valid PaymentRequestDto dto,
            @RequestHeader(value = "x-user-id") Long userId
    );
}
