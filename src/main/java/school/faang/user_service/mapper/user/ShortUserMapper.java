package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.ShortUserDto;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ShortUserMapper {
    ShortUserDto toDto(User user);
    User toEntity(ShortUserDto shortUserDto);
}
