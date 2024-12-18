package school.faang.user_service.message.event;

import school.faang.user_service.dto.user.EventSearchResponse;
import school.faang.user_service.dto.user.GoalSearchResponse;

import java.util.List;

public record UserProfileUpdateEvent(
        Long userId,
        String username,
        String country,
        String city,
        Integer experience,
        List<GoalSearchResponse> goals,
        List<String> skillNames,
        List<EventSearchResponse> events,
        Double averageRating
) {
}
