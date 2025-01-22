package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GoalInvitationDto {
    private Long id;

    @NotNull(message = "Inviter user ID must not be null")
    @Positive(message = "Inviter user ID must be positive number")
    private Long inviterId;

    @NotNull(message = "Invited user ID must not be null")
    @Positive(message = "Invited user ID must be positive number")
    private Long invitedUserId;

    @NotNull(message = "Goal ID must not be null")
    @Positive(message = "Goal ID must be positive number")
    private Long goalId;
    private RequestStatus status;
}