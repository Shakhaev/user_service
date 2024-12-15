package school.faang.user_service.dto.user_profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@Builder
@AllArgsConstructor
public class UserProfileSettingsResponseDto {
    private Long id;
    private PreferredContact preference;
    private Long userId;
}
