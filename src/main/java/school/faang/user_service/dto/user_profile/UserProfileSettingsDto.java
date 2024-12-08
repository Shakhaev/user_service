package school.faang.user_service.dto.user_profile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileSettingsDto {
    @NotNull(message = "Preferred contact is required")
    private PreferredContact preference;
}
