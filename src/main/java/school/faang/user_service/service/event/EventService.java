package school.faang.user_service.service.event;

import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.entity.event.Event;

public interface EventService {
    Event findEventById(Long id);

    TariffDto buyEventTariff(TariffDto tariffDto, Long eventId);
}
