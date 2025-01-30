package school.faang.user_service.dto.recommendation;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationDto {
    long id;
    long authorId;
    long receiverId;
    String content;
    List<SkillOfferDto> skillOffers;
    LocalDateTime createdAt;
}
