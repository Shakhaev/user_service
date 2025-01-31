package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalInvitationDto {
    private Long id;

    @NotNull(message = "Invited ID cannot be null")
    private Long inviterId;

    @NotNull(message = "Invited User ID cannot be null")
    private Long invitedUserId;

    @NotNull(message = "Goal ID cannot be null")
    private Long goalId;

    private RequestStatus status;
}
