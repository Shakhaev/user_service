package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring", uses = SkillMapper.class)
public interface GoalMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "skillsToAchieve", source = "skillsToAchieve")
    Goal toEntity(GoalDto goalDto);


    @Mapping(target = "skillsToAchieve", source = "skillsToAchieve")
    GoalDto toDto(Goal goal);
}


