package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE
)
public interface GoalMapper {
    @Mapping(source = "mentorId", target = "mentor.id", defaultExpression = "java(null)")
    @Mapping(source = "parentId", target = "parent.id", defaultExpression = "java(null)")
    Goal toEntity(GoalDto goalDto);

    @Mapping(target = "userIds", expression = "java(usersToUserIds(goal.getUsers()))")
    @Mapping(target = "skillsToAchieveIds", expression = "java(skillsToSkillIds(goal.getSkillsToAchieve()))")
    @Mapping(target = "invitationIds", expression = "java(invitationsToInvitationIds(goal.getInvitations()))")
    @InheritInverseConfiguration(name = "toEntity")
    GoalDto toDto(Goal goal);

    void updateEntityFromDto(GoalDto goalDto, @MappingTarget Goal goal);

    default List<Long> usersToUserIds(List<User> users) {
        return users.stream().map(User::getId).collect(Collectors.toList());
    }

    default List<Long> skillsToSkillIds(List<Skill> skills) {
        return skills.stream().map(Skill::getId).collect(Collectors.toList());
    }

    default List<Long> invitationsToInvitationIds(List<GoalInvitation> invitations){
        return invitations.stream().map(GoalInvitation::getId).collect(Collectors.toList());
    }
}