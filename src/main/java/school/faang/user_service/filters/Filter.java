package school.faang.user_service.filters;

import java.util.stream.Stream;

public interface Filter<Entity, Filter> {
    Stream<Entity> apply(Stream<Entity> stream, Filter filters);
}
