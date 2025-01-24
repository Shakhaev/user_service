package school.faang.user_service.dto.user;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String email;
    private String phone;
    private boolean active;
    private String aboutMe;
    private String country;
    private String city;
    private Integer experience;
    private String createdAt;
}
