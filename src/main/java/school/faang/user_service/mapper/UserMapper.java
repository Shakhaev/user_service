package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
  
    User toEntity(UserDto userDto);

    @Mapping(target = "preference", source = "contactPreference.preference")
    @Mapping(target = "countryId", source = "country.id")
    UserDto toDto(User user);
}