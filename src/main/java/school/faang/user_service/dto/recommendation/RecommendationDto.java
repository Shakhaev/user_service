package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Component
public class RecommendationDto {
    private final Long id;
    private final Long authorId;
    private final Long receiverId;
    private final String content;
    private final List<SkillOfferDto> skillOffers;
    private final LocalDateTime createdAt;
}
