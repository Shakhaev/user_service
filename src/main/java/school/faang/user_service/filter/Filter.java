package school.faang.user_service.filter;

import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface Filter<Entity, FilterDto> {
    boolean isApplicable(FilterDto filters);

    void apply(Stream<Entity> entities, FilterDto filters);
}
