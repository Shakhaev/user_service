package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDto {
    private String message;
    private List<Long> skillIds;
    private Long requesterId;
    private Long receiverId;
}