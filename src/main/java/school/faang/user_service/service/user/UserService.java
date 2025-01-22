package school.faang.user_service.service.user;

import org.apache.commons.lang3.tuple.Pair;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.entity.User;

public interface UserService {
    Pair<String, String> saveAvatarsToMinio(User user);

    UserResponseRegisterDto registerUser(UserRegisterDto dto);
}
