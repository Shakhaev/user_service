package school.faang.user_service.dto.paymentService;

import school.faang.user_service.dto.premium.PremiumPlan;

public record CreateOrderDto(
        String serviceType,
        PremiumPlan plan,
        String paymentMethod
) {
}
