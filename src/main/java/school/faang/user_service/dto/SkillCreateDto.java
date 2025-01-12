package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SkillCreateDto {

    @NotBlank(message = "Title cannot be null or empty !")
    private String title;
}
