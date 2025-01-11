package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SkillRequestDto {
    private final long id;

    private final String title;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;
}
