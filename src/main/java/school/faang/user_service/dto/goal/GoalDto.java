package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {

    private Long id;

    private Long parentId;

    @NotNull(message = "title must not be null")
    private String title;

    @NotNull(message = "description must not be null")
    private String description;

    @NotNull(message = "deadline must not be null")
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime deadline;

    private List<Long> skillsToAchieveIds;
}
