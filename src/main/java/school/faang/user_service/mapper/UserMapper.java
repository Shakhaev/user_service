package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "mentees", target = "menteesIds", qualifiedByName = "map")
    UserDto toUserDto(User user);

    @Mapping(target = "mentees", ignore = true)
    User toEntity(UserDto userDto);

    @Named("map")
    default List<Long> map(List<User> users) {
        return users.stream().map(User::getId).toList();
    }
}
