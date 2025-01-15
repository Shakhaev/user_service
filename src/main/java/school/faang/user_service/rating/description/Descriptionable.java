package school.faang.user_service.rating.description;

import school.faang.user_service.entity.User;

@FunctionalInterface
public interface Descriptionable {
    String say(User user);
}
