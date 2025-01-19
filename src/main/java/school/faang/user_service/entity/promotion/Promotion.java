package school.faang.user_service.entity.promotion;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Promotion {
    private Long id;
    private Long userId;
    private TargetType target;
    private PromotionPlan plan;
    private int impressionsLimit;
    private int currentImpressions;
    private boolean isActive;
    private LocalDateTime startTime;

    public boolean isActive() {
        return currentImpressions < impressionsLimit;
    }
}
