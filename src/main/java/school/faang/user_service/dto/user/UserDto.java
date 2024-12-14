package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.contact.PreferredContact;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Username must not be blank")
    @Size(max = 50, message = "Username must not exceed 50 characters")

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone must be a valid number")
    private String phone;

    @NotNull(message = "Updated date must not be null")
    private LocalDateTime updatedAt;

    @NotNull(message = "Preferred contact must not be null")
    private PreferredContact preference;

    @NotNull(message = "Telegram chat id must not be null")
    private Long telegramChatId;

}
