package school.faang.user_service.dto;

import lombok.Data;

@Data
public class UserFilterDto {
    private String username;
    private String email;
    private String city;
    private Boolean active;
}
