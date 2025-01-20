package school.faang.user_service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.enums.publisher.PublisherType;
import school.faang.user_service.message.broker.publisher.EventPublisher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Aspect
@Component
public class AspectEventPublisher {
    private final Map<PublisherType, EventPublisher> publishers;
    private final UserContext userContext;
    private final Executor executor;

    public AspectEventPublisher(List<EventPublisher> publishers, UserContext userContext,
                                Executor eventPublisherServicePool) {
        this.executor = eventPublisherServicePool;
        this.userContext = userContext;
        this.publishers = publishers.stream()
                .collect(Collectors.toMap(EventPublisher::getType, publisher -> publisher));
    }

    @AfterReturning(pointcut = "@annotation(publishEvent)", returning = "returnedValue",
            argNames = "joinPoint, publishEvent, returnedValue")
    public void publishEvent(JoinPoint joinPoint, PublishEvent publishEvent, Object returnedValue) {
        Long userId = userContext.getUserId();

        executor.execute(() -> execute(joinPoint, publishEvent, returnedValue, userId));
    }

    private void execute(JoinPoint joinPoint, PublishEvent publishEvent, Object returnedValue, Long userId) {
        userContext.setUserId(userId);

        EventPublisher publisher = publishers.get(publishEvent.type());
        publisher.publish(joinPoint, returnedValue);
    }
}
