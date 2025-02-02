package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserReadDto toDto(User user);
}