package school.faang.user_service.filter;

import java.util.stream.Stream;

public interface Filter<T, E> {
    boolean isApplicable(T filterDto);

    Stream<E> apply(Stream<E> entity, T filterDto);
}
