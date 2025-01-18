package school.faang.user_service.dto.recommendation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecommendationDto {
    // от RecommendationDto наследуются другие dto, отвечающие за рекомендации
    // этот класс необходим для полиморфизма и уменьшения дублирования кода
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<Long> skillOfferIds;
}
