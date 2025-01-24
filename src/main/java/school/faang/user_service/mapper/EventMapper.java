package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventResponse;
import school.faang.user_service.entity.event.Event;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    Event toEntity(EventResponse eventResponse);

    EventResponse toEventResponse(Event event);
}