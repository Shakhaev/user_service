package school.faang.user_service.filter.subscriber;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;

public interface SubscriberFilter extends Filter<User, UserFilterDto> {
}
