package school.faang.user_service.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFilterDto {
    private String usernamePattern;
    private String emailPattern;
    private String phonePattern;
    private Boolean active;
    private String aboutMePattern;
    private String countryPattern;
    private String cityPattern;
    private Integer experienceMoreThan;
    private LocalDateTime createdBefore;
}
