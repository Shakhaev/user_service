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
    User toEntity(MentorReadDto dto);

    @Mapping(source = "mentors", target = "mentorsId", qualifiedByName = "map")
    MentorReadDto toDto(User entity);

    @Named("map")
    default List<Long> map(List<User> users) {
        return users.stream().map(User::getId).toList();
    }
}
