package school.faang.user_service.rating.listeners;

import org.springframework.stereotype.Component;
import school.faang.user_service.rating.listeners.absctract.AbstractEventListener;
import school.faang.user_service.repository.UserRepository;

@Component
public class FollowingEventListener extends AbstractEventListener {
    public FollowingEventListener(UserRepository userRepository) {
        super(userRepository);
    }
}