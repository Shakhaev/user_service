package school.faang.user_service.dto.event;

import lombok.Builder;

@Builder
public record EventFilterDto(
        String titleContains,
        String startDateLaterThan,
        Integer maxAttendeesLessThan) {
}
