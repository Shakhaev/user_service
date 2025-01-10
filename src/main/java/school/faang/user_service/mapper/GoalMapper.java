package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.RequestGoalDto;
import school.faang.user_service.dto.goal.RequestGoalUpdateDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {InvitationMapper.class, SkillMapper.class})
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

 //   @Mapping(target = "parentId", source = "parent.id")
 //   @Mapping(target = "mentorId", source = "mentor.id")
  //  @Mapping(target = "invitationIds", source = "invitations", qualifiedByName = "mapInvitationsToInvitationIds")
  //  @Mapping(target = "users", source = "userIds", qualifiedByName = "mapUsersToUserIds")
 //   @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "mapSkillsToAchieveToSkillIds")
    ResponseGoalDto toDto(Goal goal);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    Goal toEntity(RequestGoalDto goalDto);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "mentorId", source = "mentor.id")
 //   @Mapping(target = "invitationIds", source = "invitations", qualifiedByName = "mapInvitationsToInvitationIds")
 //   @Mapping(target = "users", source = "userIds", qualifiedByName = "mapUsersToUserIds")
 //   @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "mapSkillsToAchieveToSkillIds")
    List<ResponseGoalDto> toDto(List<Goal> list);

    // List<Goal> toEntity(List<RequestGoalDto> list);
//    GoalUpdateDto toUpdateDto(Goal goal);

  //  @Mapping(target = "parent.id", source = "parentId")
 //   @Mapping(target = "mentor.id", source = "mentorId")
 //   @Mapping(target = "invitations", source = "invitationIds")
 //   @Mapping(target = "skillsToAchieve", source = "skillIds")
    //Goal toEntity(RequestGoalUpdateDto goalUpdateDto);

/*    @Named("mapInvitationsToInvitationIds")
    default List<Long> mapInvitationsToInvitationIds(List<GoalInvitation> invitations) {
        return invitations.stream()
                .map(GoalInvitation::getId).toList();
    }*/

 /*   @Named("mapUsersToUserIds")
    default List<Long> mapUsersToUserIds(List<User> users) {
        return users.stream()
                .map(User::getId).toList();
    }*/

/*    @Named("mapSkillsToAchieveToSkillIds")
    default List<Long> mapSkillsToAchieveToSkillIds(List<Skill> skillsToAchieve) {
        return skillsToAchieve.stream()
                .map(Skill::getId).toList();
    }*/

}