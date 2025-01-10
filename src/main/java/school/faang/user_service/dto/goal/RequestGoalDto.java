package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestGoalDto {
    private Long id;
    private Long parentId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private Long mentorId;
}