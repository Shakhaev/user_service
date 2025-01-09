package school.faang.user_service.filters.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.Filter;

public interface UserFilter extends Filter<User, UserFilterDto> {
}
