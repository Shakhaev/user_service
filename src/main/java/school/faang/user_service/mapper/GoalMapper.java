package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.RequestGoalDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    ResponseGoalDto toDto(Goal goal);

    @Mapping(target = "id", ignore = true)
    Goal toEntity(RequestGoalDto goalDto);

    List<ResponseGoalDto> toDto(List<Goal> list);
}