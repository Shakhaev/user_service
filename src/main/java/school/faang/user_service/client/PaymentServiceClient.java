package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.payment.CreateOrderDto;
import school.faang.user_service.dto.payment.OrderDto;

@FeignClient(name = "paymentService")
public interface PaymentServiceClient {

    @PostMapping("order")
    OrderDto createOrder(@RequestBody CreateOrderDto dto);

    @GetMapping("order/{orderId}")
    OrderDto getOrder(@PathVariable Long orderId);
}