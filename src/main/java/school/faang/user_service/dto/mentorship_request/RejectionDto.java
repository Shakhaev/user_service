package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RejectionDto {

    @NotBlank
    @Size(min = 1, max = 4096, message = "Причина отклонения запроса должна иметь длину от 1 до 4096 символов")
    private final String reason;
}
