package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class GoalInvitationDto {
    private Long id;
    @NotNull(message = "Поле Id не может быть равным нулю")
    @Positive(message = "Поле Id должно быть положительным числом")
    private Long inviterId;
    @NotNull(message = "Поле Id не может быть равным нулю")
    @Positive(message = "Поле Id должно быть положительным числом")
    private Long invitedUserId;
    @NotNull(message = "Поле Id не может быть равным нулю")
    @Positive(message = "Поле Id должно быть положительным числом")
    private Long goalId;
    private RequestStatus status;

}
