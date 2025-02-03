package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class SkillOfferDto {
    private Long id;
    private Long skillId;
}
