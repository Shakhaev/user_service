package school.faang.user_service.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserScoreDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserScoreMapper {

    @Mapping(target = "userID", source = "id")
    @Mapping(target = "experience", source = "experience")
    @Mapping(target = "ownedEventsId", source = "ownedEvents.id")
    @Mapping(target = "menteesId", source = "mentees.id")
    @Mapping(target = "goalsId", source = "goals.id")
    @Mapping(target = "skillsId", source = "skills.id")
    @Mapping(target = "participatedEventsId", source = "participatedEvents.id")
    @Mapping(target = "recommendationsReceivedId", source = "recommendationsReceived.id")
    List<UserScoreDto> toDto(List<User> users);

    @IterableMapping(elementTargetType = Long.class)
    List<Long> mapOwnedEventsToIds(List<Event> ownedEvents);

    @IterableMapping(elementTargetType = Long.class)
    List<Long> mapMenteesToIds(List<User> mentees);

    @IterableMapping(elementTargetType = Long.class)
    List<Long> mapGoalsToIds(List<Goal> goals);

    @IterableMapping(elementTargetType = Long.class)
    List<Long> mapSkillsToIds(List<Skill> skills);

    @IterableMapping(elementTargetType = Long.class)
    List<Long> mapParticipatedEventsToIds(List<Event> participatedEvents);

    @IterableMapping(elementTargetType = Long.class)
    List<Long> mapRecommendationsReceivedToIds(List<Recommendation> recommendationsReceived);
}
