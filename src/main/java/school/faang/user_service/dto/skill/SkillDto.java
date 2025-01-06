package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkillDto {
    private Long id;
    @NotBlank(message = "Название умения не может быть пустым")
    @Size(max = 64, message = "Название умения должно быть не более 64 символов")
    private String title;
}
