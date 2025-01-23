package school.faang.user_service.filter;

import java.util.stream.Stream;

public interface Filter<E, F> {
    boolean isApplicable(F filters);

    Stream<E> apply(Stream<E> entities, F filters);
}
