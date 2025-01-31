package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    private Long id;
    @NotBlank
    @Size(max = 64, message = "Название skill не может быть больше 25 символов!")
    @Pattern(regexp = ".*[a-zA-Zа-яА-ЯёЁ]+.*", message = "Название skill не может содержать только цифры!")
    private String title;
}
