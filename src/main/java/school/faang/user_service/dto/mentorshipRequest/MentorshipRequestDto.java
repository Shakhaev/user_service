package school.faang.user_service.dto.mentorshipRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {
    private Long id;
    @NotBlank(message = "Описание запроса на менторство не может быть пустым.")
    private String description;
    @Positive(message = "Поле Id должно быть положительным числом")
    @NotNull(message = "Поле Id не может равняться нулю")
    private Long requesterId;
    @Positive(message = "Поле Id должно быть положительным числом")
    @NotNull(message = "Поле Id не может равняться нулю")
    private Long receiverId;
    private RequestStatus status;
    private String rejectionReason;
}
