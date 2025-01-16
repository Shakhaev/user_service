package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import school.faang.user_service.entity.RequestStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoalInvitationDto {
    private static final String NOT_BE_NULL = "ID mustn't be null";
    private static final String MUST_BE_POSITIVE = "ID must be a positive number";

    @NonNull
    private Long id;

    @NonNull
    private Long inviterId;

    @NotNull(message = NOT_BE_NULL)
    @Positive(message = MUST_BE_POSITIVE)
    private Long invitedUserId;

    @NotNull(message = NOT_BE_NULL)
    @Positive(message = MUST_BE_POSITIVE)
    private Long goalId;
    private RequestStatus status;
}