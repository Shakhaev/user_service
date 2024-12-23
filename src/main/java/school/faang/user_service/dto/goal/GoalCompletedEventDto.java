package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalCompletedEventDto {
    @NotNull
    private Long userId;

    @NotNull
    private Long goalId;

    @NotNull
    private LocalDateTime completedAt;
}
