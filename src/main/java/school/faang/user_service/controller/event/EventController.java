package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.BuyTariffRequest;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.properties.UserServiceProperties;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/event")
public class EventController {
    private final UserServiceProperties userServiceProperties;
    private final EventService eventService;

    @PostMapping("/buy-tariff")
    public ResponseEntity<TariffDto> buyTariff(@RequestBody BuyTariffRequest request) {
        return ResponseEntity.ok(eventService.buyEventTariff(request.tariffDto(), request.id()));
    }

    @GetMapping("/tariffs")
    public List<TariffDto> getAvailableTariffs() {
        return userServiceProperties.getListAvailableTariffDtos();
    }

    @GetMapping("/events")
    public List<EventDto> getUsers(@RequestBody GetEventRequest request) {
        return eventService.findEventByFilter(request.getFilter(), request.getLimit(), request.getOffset());
    }
}
