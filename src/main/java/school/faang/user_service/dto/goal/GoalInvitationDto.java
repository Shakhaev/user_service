package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;

@Component
@Getter
@Setter
public class GoalInvitationDto {
    private Long id;
    private Long inviterId;
    private Long invitedUserId;
    private Long goalId;
    private RequestStatus status;
}
