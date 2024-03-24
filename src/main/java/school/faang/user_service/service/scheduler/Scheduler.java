package school.faang.user_service.service.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.event.EventService;


@Service
@RequiredArgsConstructor
public class Scheduler {
    private final EventService eventService;

    @Scheduled(cron = "${clearEvents.cron}")
    @Transactional
    public void clearEvents() {
        eventService.clearEvents();
    }
}
