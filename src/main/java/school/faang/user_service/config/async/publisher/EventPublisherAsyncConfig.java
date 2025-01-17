package school.faang.user_service.config.async.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class EventPublisherAsyncConfig {
    @Value("${app.async-config.event_publisher.core_pool_size}")
    private int corePoolSize;

    @Value("${app.async-config.event_publisher.max_pool_size}")
    private int maxPoolSize;

    @Value("${app.async-config.event_publisher.queue_capacity}")
    private int queueCapacity;

    @Value("${app.async-config.event_publisher.thread_name_prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor eventPublisherServicePool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);

        return executor;
    }
}
