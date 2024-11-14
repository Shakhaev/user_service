package school.faang.user_service.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.client.dto.ResourceDocumentResponseDto;

import java.util.List;

@FeignClient(
        name = "promotion-service",
        url = "${promotion-service.service.url",
        configuration = FeignConfig.class
)
public interface PromotionClient {

    @GetMapping("/api/v1/promotions/search")
    List<ResourceDocumentResponseDto> getPromotions(
            @RequestParam("requiredResCount") @Positive Integer requiredResCount,
            @RequestParam("sessionId") @NotBlank String sessionId
    );
}
