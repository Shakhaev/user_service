package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    private String name;
    private String about;
    private String email;
    private String contact;
    private String country;
    private String city;
    private String phone;
    private String skill;
    private Integer experienceMin;
    private Integer experienceMax;
    private int page = 1;
    private int pageSize = 10;
}
