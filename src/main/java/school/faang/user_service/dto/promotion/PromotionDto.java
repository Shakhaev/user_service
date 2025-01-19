package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.entity.promotion.TargetType;

import java.time.LocalDateTime;

@Data
@Builder
public class PromotionDto {

    @NotNull(message = "ID is required")
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Target is required")
    private TargetType target;

    @NotNull(message = "Plan is required")
    private PromotionType plan;

    @Min(value = 1, message = "Impressions limit must be at least 1")
    private int impressionsLimit;

    @Min(value = 0, message = "Current impressions cannot be negative")
    private int currentImpressions;

    private boolean isActive;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
}
