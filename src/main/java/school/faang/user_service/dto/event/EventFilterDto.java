package school.faang.user_service.dto.event;

public record EventFilterDto(
        String titleContains,
        String startDateLaterThan,
        Integer maxAttendeesLessThan) {
}
