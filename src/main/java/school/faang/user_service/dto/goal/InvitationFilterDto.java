package school.faang.user_service.dto.goal;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;

@Component
@Getter
@Setter
public class InvitationFilterDto {
    private String inviterNamePattern;
    private String invitedNamePattern;
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
}
