package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.RequestGoalDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent.id", source = "parentId")
    @Mapping(target = "mentor.id", source = "mentorId")
    Goal toEntity(RequestGoalDto goalDto);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "mentorId", source = "mentor.id")
    ResponseGoalDto toDto(Goal goal);

    List<ResponseGoalDto> toDto(List<Goal> list);
}