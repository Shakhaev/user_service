package school.faang.user_service.aspect;

import school.faang.user_service.enums.publisher.PublisherType;

public interface EventPublisher {
    PublisherType getType();

    void publish(Object eventObject);
}
