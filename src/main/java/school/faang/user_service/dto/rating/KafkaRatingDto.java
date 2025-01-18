package school.faang.user_service.dto.rating;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.Positive;

public record KafkaRatingDto(
        @NonNull @Positive long id
) { }