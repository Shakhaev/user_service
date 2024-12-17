package school.faang.user_service.filter.user;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public interface UserFilter {
    boolean apply(User user, UserFilterDto userFilterDto);
}