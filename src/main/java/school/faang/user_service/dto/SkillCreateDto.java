package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkillCreateDto {

    @NotBlank(message = "Назавание не может быть пустым или NULL .")
    private String title;
}
