package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.contact.PreferredContact;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Long telegramChatId;
    private PreferredContact preference;
    private String phone;

    public UserDto(Long id, String username, String email, Long telegramChatId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.telegramChatId = telegramChatId;
    }

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }

}
