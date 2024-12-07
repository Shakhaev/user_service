package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationUserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;
}