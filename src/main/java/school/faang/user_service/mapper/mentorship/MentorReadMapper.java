package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorReadDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorReadMapper {
    @Mapping(target = "mentors", ignore = true)
    User toEntity(MentorReadDto dto);

    @Mapping(source = "mentors", target = "mentorsId", qualifiedByName = "mapToIds")
    MentorReadDto toDto(User entity);

    @Named("mapToIds")
    default List<Long> map(List<User> users) {
        return users.stream().map(User::getId).toList();
    }
}
