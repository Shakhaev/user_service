package school.faang.user_service.aspect;

import org.aspectj.lang.JoinPoint;
import school.faang.user_service.enums.publisher.PublisherType;

public interface EventPublisher {
    PublisherType getType();

    void publish(JoinPoint joinPoint, Object returnedValue);
}
