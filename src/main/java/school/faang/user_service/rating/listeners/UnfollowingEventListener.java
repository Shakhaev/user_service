package school.faang.user_service.rating.listeners;

import org.springframework.stereotype.Component;
import school.faang.user_service.rating.listeners.absctract.AbstractEventListener;
import school.faang.user_service.repository.UserRepository;

@Component
public class UnfollowingEventListener extends AbstractEventListener {
    public UnfollowingEventListener(UserRepository userRepository) {
        super(userRepository);
    }
}