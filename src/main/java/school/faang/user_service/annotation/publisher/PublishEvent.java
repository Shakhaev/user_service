package school.faang.user_service.annotation.publisher;

import school.faang.user_service.enums.publisher.PublisherType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PublishEvent {
    PublisherType type();
}
