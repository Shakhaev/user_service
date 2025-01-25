package school.faang.user_service.controller.user;

import lombok.Data;
import school.faang.user_service.dto.user.UserDto;

@Data
public class GetUserRequest {
    private UserDto filter;
    private Integer limit;
    private Integer offset;
}
