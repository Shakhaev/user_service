package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.MaxAttendeesLessThanFilter;
import school.faang.user_service.filter.event.StartDateLaterThanFilter;
import school.faang.user_service.filter.event.TitleContainsFilter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TestData {
    public static Event createEvent(Long id, String title, String date, int maxAttendees ) {
        return Event.builder()
                .id(id)
                .title(title)
                .startDate(LocalDateTime.parse(date))
                .maxAttendees(maxAttendees)
                .build();
    }

    public static EventRequestDto createEventRequestDto(String title, String date, Long id) {
        return EventRequestDto.builder()
                .title(title)
                .startDate(LocalDateTime.parse(date))
                .ownerId(id)
                .build();
    }

    public static EventRequestDto createEventRequestDto(String title, Long id, List<Long> ids) {
        return EventRequestDto.builder()
                .title(title)
                .ownerId(id)
                .relatedSkillsIds(ids)
                .build();
    }

    public static EventFilterDto createEventFilterDto(String charSequence, String date, int attendees) {
        return EventFilterDto.builder()
                .titleContains(charSequence)
                .startDateLaterThan(LocalDateTime.parse(date))
                .maxAttendeesLessThan(attendees)
                .build();
    }

    public static List<EventFilter> createFilters() {
        TitleContainsFilter titleContainsFilter = new TitleContainsFilter();
        StartDateLaterThanFilter startDateLaterThanFilter = new StartDateLaterThanFilter();
        MaxAttendeesLessThanFilter maxAttendeesLessThanFilter = new MaxAttendeesLessThanFilter();

        return new ArrayList<>(List.of(titleContainsFilter, startDateLaterThanFilter, maxAttendeesLessThanFilter));
    }

    public static User createUser(Long id, List<Skill> skills) {
        return User.builder()
                .id(id)
                .skills(skills)
                .build();
    }
}
