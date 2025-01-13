package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MenteeReadDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenteeReadMapper {
    @Mapping(target = "mentees", ignore = true)
    User toEntity(MenteeReadDto dto);

    @Mapping(source = "mentees", target = "menteesId", qualifiedByName = "mapToIds")
    MenteeReadDto toDto(User entity);

    @Named("mapToIds")
    default List<Long> map(List<User> users) {
        return users.stream().map(User::getId).toList();
    }
}
