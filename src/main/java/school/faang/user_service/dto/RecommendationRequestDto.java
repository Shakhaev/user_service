package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDto {
    private final Long id;
    private final String message;
    private final String status;
    private final List<SkillDto> skills;
    private final Long requesterId;
    private final Long receiverId;
    private final String createdAt;
    private final String updatedAt;
}
