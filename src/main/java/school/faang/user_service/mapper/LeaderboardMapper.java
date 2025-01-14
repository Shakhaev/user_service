package school.faang.user_service.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LeaderboardMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "activityScore", expression = "java(0)")
    @Mapping(target = "experience", source = "experience")
    @Mapping(target = "ownedEventsId", source = "ownedEvents")
    @Mapping(target = "menteesId", source = "mentees")
    @Mapping(target = "goalsId", source = "goals")
    @Mapping(target = "skillsId", source = "skills")
    @Mapping(target = "participatedEventsId", source = "participatedEvents")
    @Mapping(target = "recommendationsReceivedId", source = "recommendationsReceived")
    LeaderboardDto toDto(User user);

    @IterableMapping(elementTargetType = Long.class)
    default List<Long> mapEventsToIds(List<Event> events) {
        return events.stream().map(Event::getId).collect(Collectors.toList());
    }

    @IterableMapping(elementTargetType = Long.class)
    default List<Long> mapMenteesToIds(List<User> mentees) {
        return mentees.stream().map(User::getId).collect(Collectors.toList());
    }

    @IterableMapping(elementTargetType = Long.class)
    default List<Long> mapGoalsToIds(List<Goal> goals) {
        return goals.stream().map(Goal::getId).collect(Collectors.toList());
    }

    @IterableMapping(elementTargetType = Long.class)
    default List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills.stream().map(Skill::getId).collect(Collectors.toList());
    }

    @IterableMapping(elementTargetType = Long.class)
    default List<Long> mapRecommendationsReceivedToIds(List<Recommendation> recommendationsReceived) {
        return recommendationsReceived.stream().map(Recommendation::getId).collect(Collectors.toList());
    }

}
