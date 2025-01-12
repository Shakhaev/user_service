package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    @Mapping(target = "userIds", expression = "java(usersToUserIds(goal.getUsers()))")
    @Mapping(target = "skillsToAchieveIds", expression = "java(skillsToSkillIds(goal.getSkillsToAchieve()))")
    @Mapping(target = "invitationIds", expression = "java(invitationsToInvitationIds(goal.getInvitations()))")
    ResponseGoalDto toDto(Goal goal);

    List<ResponseGoalDto> toDto(List<Goal> list);

    default List<Long> usersToUserIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    default List<Long> invitationsToInvitationIds(List<GoalInvitation> invitations) {
        return invitations.stream()
                .map(GoalInvitation::getId)
                .collect(Collectors.toList());
    }

    default List<Long> skillsToSkillIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}
