package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Identifiable;
import school.faang.user_service.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface UserMapper {

    UserDto toDto(User user);

    default Long toId(Identifiable identifiable) {
        if (identifiable == null) {
            return null;
        }
        return identifiable.getId();
    }
}
