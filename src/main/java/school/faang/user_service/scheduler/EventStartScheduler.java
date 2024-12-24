package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.scheduler.SchedulerEventStartNotificationConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.event.EventStartEvent;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.service.RedisService;
import school.faang.user_service.service.event.EventService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStartScheduler {
    private static final int TIME_FROM_KEY = 2;
    private static final String DATA_DIVIDER = ":";

    private final EventService eventService;
    private final RedisTemplate<String, Object> lettuceRedisTemplate;
    private final SchedulerEventStartNotificationConfig schedulerConfig;
    private final RedisService redisService;
    private final EventStartEventPublisher eventStartEventPublisher;

    @Value("${scheduler.event-start-notification-config.event-fetch-days-before-start}")
    private int daysTo;

    @Scheduled(cron = "${cron.expressions.loadUpcomingEvents}")
    public void loadUpcomingEvents() {
        log.info("Loading upcoming events started at {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime to = now.plusDays(daysTo);
        List<Event> events = eventService.getEventsByStartDateBetween(now, to);
        log.info("Found {} events between {} and {}", events.size(), now, to);
        events.forEach(event -> {
            List<SchedulerEventStartNotificationConfig.NotificationConfig> notifications = schedulerConfig.getNotifications();
            notifications.forEach(notification -> {
                Duration duration = Duration.parse(notification.getTime());
                LocalDateTime notifyTime = event.getStartDate().minus(duration);
                String key = String.format("event:%d:%d", event.getId(), notifyTime.toEpochSecond(ZoneOffset.UTC));
                redisService.saveValue(key, event.getId());
                log.info("Saved notification trigger for event {} at {}", event.getId(), notifyTime);
            });
        });
    }


    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void publishPendingEventStartEvents() {
        Set<String> keys = redisService.getKeysByPattern("event:*:*");
        log.info("Find {} pending event to publish notifications", keys.size());
        keys.forEach(key -> {
            try {
                String[] parts = key.split(DATA_DIVIDER);
                long notifyTime = Long.parseLong(parts[TIME_FROM_KEY]);
                long currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

                if (currentTime >= notifyTime) {
                    Optional.ofNullable(lettuceRedisTemplate.opsForValue().get(key))
                            .map(value -> (Integer) value)
                            .ifPresent(eventId -> {
                                Event event = eventService.findEventById(eventId);
                                List<User> attendees = event.getAttendees();
                                EventStartEvent eventStartEvent =
                                        new EventStartEvent(event.getId(), attendees.stream()
                                                .map(User::getId)
                                                .toList()
                                        );
                                eventStartEventPublisher.publish(eventStartEvent);
                                redisService.deleteKey(key);
                            });
                }
            } catch (Exception e) {
                log.error("Error publishing event start event", e);
            }
        });
    }
}
