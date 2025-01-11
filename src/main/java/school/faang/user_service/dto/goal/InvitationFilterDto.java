package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationFilterDto {
    @JsonProperty("inviterNamePattern")
    private String inviterNamePattern;

    @JsonProperty("invitedNamePattern")
    private String invitedNamePattern;

    @JsonProperty("inviterId")
    private Long inviterId;

    @JsonProperty("invitedId")
    private Long invitedId;

    @JsonProperty("status")
    private RequestStatus status;
}
