package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "promotion-service",
        url = "${promotion-service.host}:${promotion-service.port}"
)
public interface PromotionServiceClient {
    @GetMapping("/promotion/users")
    List<Long> getPromotionUsers();

    @GetMapping("/promotion/events")
    List<Long> getPromotionEvents();
}
