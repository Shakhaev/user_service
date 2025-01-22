package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {

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
}
