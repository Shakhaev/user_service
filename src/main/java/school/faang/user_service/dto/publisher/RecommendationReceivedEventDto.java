package school.faang.user_service.dto.publisher;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecommendationReceivedEventDto {
    private Long id;
    private Long authorId;
    private String authorName;
    private Long receiverId;
    private LocalDateTime timestamp;
}
