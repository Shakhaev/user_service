package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectionDto {

    @NotBlank
    @Size(min = 1, max = 4096, message = "Причина отклонения запроса должна иметь длину от 1 до 4096 символов")
    private String reason;
}
