package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MentorshipRequestDto {

    @Positive(message = "Id должен быть больше, чем 0")
    private final Long requesterId;

    @Positive(message = "Id должен быть больше, чем 0")
    private final Long receiverId;

    @NotBlank
    @Size(min = 1, max = 4096, message = "Описание запроса должно иметь длину от 1 до 4096 символов")
    private final String description;
}
