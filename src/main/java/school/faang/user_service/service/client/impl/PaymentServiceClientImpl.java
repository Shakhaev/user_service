package school.faang.user_service.service.client.impl;

import org.springframework.cloud.openfeign.FeignClient;
import school.faang.user_service.service.client.PaymentServiceClient;


@FeignClient(name = "payment-service", url = "", path = "")
public class PaymentServiceClientImpl implements PaymentServiceClient {
}
