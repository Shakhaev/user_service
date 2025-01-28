package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
public class InvitationFilterDto {
    @NotNull
    private String inviterNamePattern;
    @NotNull
    private String invitedNamePattern;
    @NotNull
    private Long inviterId;
    @NotNull
    private Long invitedId;
    private RequestStatus status;
}