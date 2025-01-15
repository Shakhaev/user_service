package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.paymentService.CreateOrderDto;

@FeignClient("paymentService")
public interface PaymentServiceClient {

    @PostMapping("order")
    String createPayment(@RequestBody CreateOrderDto dto);
}
