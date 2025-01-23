package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
public class GoalInvitationDto {
    @NotNull
    private Long id;
    @NotNull
    private Long inviterId;
    @NotNull
    private Long invitedUserId;
    @NotNull
    private Long goalId;
    private RequestStatus status;
}