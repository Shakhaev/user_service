package school.faang.user_service.service.notification;

import school.faang.user_service.entity.user.User;

import java.util.List;

public interface NotificationService {

    void publish(List<User> users);
}
