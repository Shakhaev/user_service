package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RejectionDto {

    @NotBlank(message = "Поле не может быть пустым.")
    @Size(max = 4096, message = "Текст не может превышать 4096 символов.")
    private String rejectionReason;
}
