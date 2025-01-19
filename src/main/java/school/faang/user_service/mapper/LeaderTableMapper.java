package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.rating.LeaderTableDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface LeaderTableMapper {
    User toEntity(LeaderTableDto tableDto);

    LeaderTableDto toDto(User user);
}
