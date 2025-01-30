package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.CreateGoalResponse;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalResponse;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.SkillMapper;

@Mapper(componentModel = "spring", uses = {SkillMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "parent.id", target = "parentId"),
            @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToIds")
    })
    GoalDto toDto(Goal goal);

    @Mappings({
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "parentId", target = "parent.id"),
            @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapIdsToSkills")
    })
    Goal toEntity(GoalDto goalDto);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "parent.id", target = "parentId"),
            @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToIds"),
            @Mapping(source = "createdAt", target = "createdAt")
    })
    CreateGoalResponse toCreateResponse(Goal goal);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "parent.id", target = "parentId"),
            @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToIds"),
            @Mapping(source = "updatedAt", target = "updatedAt")
    })
    UpdateGoalResponse toUpdateResponse(Goal goal);
}