package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.UserFilter;

@Mapper(componentModel = "spring")
public interface UserFilterMapper {
    UserFilter toEntity(UserFilterDto userFilterDto);
}
