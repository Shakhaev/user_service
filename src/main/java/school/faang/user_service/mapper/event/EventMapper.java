package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event toEntity(EventDto eventDto);

    EventDto toDto(Event event);
}
