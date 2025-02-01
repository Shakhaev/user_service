package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.user.UserDto;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import java.util.List;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User toEntity(UserDto dto);

    UserDto toDto(User user);
    List<UserDto> toDto(List<User> users);

}