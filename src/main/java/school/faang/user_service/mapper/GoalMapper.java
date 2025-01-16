package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.CreateGoalRequest;
import school.faang.user_service.dto.goal.GoalResponse;
import school.faang.user_service.dto.goal.UpdateGoalRequest;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GoalMapper {
    @Mapping(target = "userIds", expression = "java(usersToUserIds(goal.getUsers()))")
    @Mapping(target = "skillsToAchieveIds", expression = "java(skillsToSkillIds(goal.getSkillsToAchieve()))")
    @Mapping(target = "invitationIds", expression = "java(invitationsToInvitationIds(goal.getInvitations()))")
    GoalResponse toResponse(Goal goal);

    void updateEntityFromDto(CreateGoalRequest goalRequest, @MappingTarget Goal goal);

    default List<Long> usersToUserIds(List<User> users) {
        return users == null ? Collections.emptyList() : users.stream().map(User::getId).collect(Collectors.toList());
    }

    default List<Long> skillsToSkillIds(List<Skill> skills) {
        return skills == null ? Collections.emptyList() : skills.stream().map(Skill::getId).collect(Collectors.toList());
    }

    default List<Long> invitationsToInvitationIds(List<GoalInvitation> invitations) {
        return invitations == null ? Collections.emptyList() : invitations.stream().map(GoalInvitation::getId).collect(Collectors.toList());
    }

    @Mapping(source = "mentorId", target = "mentor.id")
    @Mapping(source = "parentId", target = "parent.id")
    Goal toEntity(UpdateGoalRequest updateGoalRequest);

    void updateEntityFromDto(UpdateGoalRequest goalDto, @MappingTarget Goal goal);
}