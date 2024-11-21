package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.EventSearchResponse;
import school.faang.user_service.model.jpa.event.Event;
import school.faang.user_service.model.jpa.event.EventStatus;
import school.faang.user_service.model.jpa.event.EventType;
import school.faang.user_service.model.search.user.EventNested;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    EventSearchResponse toSearchResponse(EventNested searchResponse);

    @Mapping(source = "owner.username", target = "usernameOwner")
    EventSearchResponse toSearchResponse(Event event);

    @Mapping(source = "owner.username", target = "usernameOwner")
    EventNested toEventNested(Event event);

    default String mapEventType(EventType eventType) {
        return eventType != null ? eventType.name() : null;
    }

    default String mapEventStatus(EventStatus eventStatus) {
        return eventStatus != null ? eventStatus.name() : null;
    }
}
