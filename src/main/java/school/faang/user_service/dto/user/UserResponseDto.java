package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.user.message.ProfileViewEventParticipant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto implements ProfileViewEventParticipant {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private boolean active;
    private String aboutMe;
    private String preference;
}
