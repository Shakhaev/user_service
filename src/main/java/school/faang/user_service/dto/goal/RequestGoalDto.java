package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RequestGoalDto {
    private Long id;
    @NonNull
    private Long parentId;
    @NotBlank
    private String title;
    private String description;
    @NonNull
    private GoalStatus status;
    @FutureOrPresent
    private LocalDateTime deadline;
    private Long mentorId;
}