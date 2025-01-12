package school.faang.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    private String namePattern;
    private String aboutPattern;
    private String emailPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String phonePattern;
    private String skillPattern;
    private int experienceMin = 0;
    private int experienceMax = Integer.MAX_VALUE;
    private int page = 0;
    private int pageSize = 10;
}
