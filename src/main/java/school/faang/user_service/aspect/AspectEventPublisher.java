package school.faang.user_service.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.enums.publisher.PublisherType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Aspect
@Component
public class AspectEventPublisher {
    private final Map<PublisherType, EventPublisher> publishers;
    private final Executor executor;

    public AspectEventPublisher(Executor eventPublisherServicePool, List<EventPublisher> publishers) {
        this.executor = eventPublisherServicePool;
        this.publishers = publishers.stream()
                .collect(Collectors.toMap(EventPublisher::getType, publisher -> publisher));
    }

    @AfterReturning(pointcut = "@annotation(publishEvent)", returning = "returnedValue")
    public void publishEvent(PublishEvent publishEvent, Object returnedValue) {
        executor.execute(() -> execute(publishEvent, returnedValue));
    }

    private void execute(PublishEvent publishEvent, Object returnedValue) {
        EventPublisher publisher = publishers.get(publishEvent.type());
        publisher.publish(returnedValue);
    }
}
