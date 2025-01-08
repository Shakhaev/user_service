package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SkillDto {

    private Long id;

    @NotBlank(message = "Назавание не может быть пустым или NULL .")
    private String title;
    private List<Long> userIds;
}
