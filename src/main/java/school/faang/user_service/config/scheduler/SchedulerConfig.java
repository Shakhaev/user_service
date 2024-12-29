package school.faang.user_service.config.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SchedulerConfig {

    @Value("${scheduler.clear-events.thread-pool}")
    private int pool;

    @Bean
    public ExecutorService clearEventsThreadPool() {
        return Executors.newFixedThreadPool(pool);
    }
}
