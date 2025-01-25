package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class UserSkillGuaranteeDto {
    Long id;
    Long userId;
    Long skillId;
    Long guarantorId;
}
