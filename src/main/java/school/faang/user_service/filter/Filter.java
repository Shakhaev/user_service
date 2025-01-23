package school.faang.user_service.filter;

import java.util.stream.Stream;

public interface Filter<Entity, FilterDto> {
    boolean isApplicable(FilterDto filters);

    Stream<Entity> apply(Stream<Entity> entities, FilterDto filters);
}
