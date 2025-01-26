package school.faang.user_service.dto.mentorship_request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MentorshipRequestDto {

    @Nullable
    private Long id;

    @Nullable
    private RequestStatus status;

    @Positive(message = "Id должен быть больше, чем 0")
    private Long requesterId;

    @Positive(message = "Id должен быть больше, чем 0")
    private Long receiverId;

    @NotBlank
    @Size(min = 1, max = 4096, message = "Описание запроса должно иметь длину от 1 до 4096 символов")
    private String description;
}
