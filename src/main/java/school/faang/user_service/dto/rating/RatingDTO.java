package school.faang.user_service.dto.rating;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.description.Descriptionable;

@Data
@RequiredArgsConstructor
public class RatingDTO {
    private final Descriptionable descriptionable;
    private final User user;
    private final int points;
    private final ActionType actionType;
}
