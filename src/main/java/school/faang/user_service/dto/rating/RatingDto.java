package school.faang.user_service.dto.rating;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.Positive;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.description.Descriptionable;

public record RatingDto(
        @NonNull Descriptionable descriptionable,
        @NonNull @Positive long id,
        @NonNull @Positive int points,
        @NonNull ActionType actionType
) {}