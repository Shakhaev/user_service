package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SkillDto(
        Long id,
        @NotBlank String title
) {

}
