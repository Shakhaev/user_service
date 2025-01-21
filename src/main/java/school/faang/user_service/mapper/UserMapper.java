package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserRegisterRequest;
import school.faang.user_service.dto.UserRegisterResponse;
import school.faang.user_service.entity.User;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    User toEntity(UserDto dto);

    @Mapping(source = "countryId", target = "country.id")
    User toEntity(UserRegisterRequest request);

    UserDto toDto(User entity);

    UserRegisterResponse toUserRegisterResponse(User user);
}