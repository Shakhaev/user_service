package school.faang.user_service.mapper;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class UserMapper {

    public List<UserDto> toDtos(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
