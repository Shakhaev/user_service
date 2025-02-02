package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.service.promotion.PromotionPaymentService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/payments")
public class PromotionPaymentController {
    private final PromotionPaymentService promotionPaymentService;

    @GetMapping("/{id}")
    public PromotionPaymentDto getPromotionPaymentById(@PathVariable("id") UUID id) {
        return promotionPaymentService.getPromotionPaymentById(id);
    }
}
