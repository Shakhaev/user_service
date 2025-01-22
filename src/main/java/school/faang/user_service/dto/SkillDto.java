package school.faang.user_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SkillDto {

    private Long id;

    @NotEmpty(message = "title must not be empty")
    @NotNull(message = "title must not be null")
    private String title;
    private List<Long> userId;
}