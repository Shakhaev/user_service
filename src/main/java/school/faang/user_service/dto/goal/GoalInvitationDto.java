package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalInvitationDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("inviterId")
    private Long inviterId;

    @JsonProperty("invitedUserId")
    private Long invitedUserId;

    @JsonProperty("goalId")
    private Long goalId;

    @JsonProperty("status")
    private RequestStatus status;
}
