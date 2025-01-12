package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class GoalInvitationFilterDto {
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
    private String inviterNamePattern;
    private String invitedNamePattern;
}
