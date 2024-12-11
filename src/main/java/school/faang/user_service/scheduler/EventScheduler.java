package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventScheduler {

    private final EventService eventService;

    @Value("${scheduler.clear-events.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${scheduler.clear-events.cron}")
    @Async("clearEventsThreadPool")
    public void clearEvents() {

        List<EventDto> events = eventService.getPastEventsIds();

        if (!events.isEmpty()) {
            List<Long> eventsIds = events.stream().map(EventDto::getId).toList();

            List<CompletableFuture<Void>> eventDeleteFuture = ListUtils
                    .partition(eventsIds, batchSize).stream()
                    .map(this::delete)
                    .toList();

            CompletableFuture.allOf(eventDeleteFuture.toArray(new CompletableFuture[0]))
                    .thenRun(() -> log.info("Past events deleted."));

        } else {
            log.info("No events to delete.");
        }
    }

    public CompletableFuture<Void> delete(List<Long> ids) {
        return CompletableFuture.runAsync(() -> {
            for (Long id : ids){
                eventService.deleteEvent(id);
                log.debug("Event deleted");
            }
        });
    }

}
