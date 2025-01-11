package school.faang.user_service.service.user;

import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> findByIds(Set<Long> ids);
}
