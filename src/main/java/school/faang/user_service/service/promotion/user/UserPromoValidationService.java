package school.faang.user_service.service.promotion.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.promotion.UserAlreadyHasPromotionException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserPromoValidationService {
    public void checkUserForPromotion(User user) {
        getActiveUserPromotion(user).ifPresent(promotion -> {
            throw new UserAlreadyHasPromotionException(user.getId(), promotion.getNumberOfViews());
        });
    }

    public Optional<UserPromotion> getActiveUserPromotion(User user) {
        return user.getPromotions()
                .stream()
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .findFirst();
    }

    public List<UserPromotion> getActiveUserPromotions(List<User> users) {
        return users.stream()
                .map(this::getActiveUserPromotion)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
