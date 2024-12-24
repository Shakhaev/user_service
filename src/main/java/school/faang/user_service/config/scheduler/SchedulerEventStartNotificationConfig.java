package school.faang.user_service.config.scheduler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "scheduler.event-start-notification-config")
@Data
public class SchedulerEventStartNotificationConfig {
    private List<NotificationConfig> notifications;

    @Data
    public static class NotificationConfig {
        private String stage;
        private String time;
    }
}
