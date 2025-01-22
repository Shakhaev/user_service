package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseRegisterDto {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Email
    @NotNull
    private String email;

    private String phone;

    private String aboutMe;

    private boolean active;

    private String city;

    private long countryId;

    private Integer experience;

    private LocalDateTime createdAt;

    private String avatarId;

    private String avatarSmallId;
}
