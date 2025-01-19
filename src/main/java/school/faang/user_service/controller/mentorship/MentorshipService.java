package school.faang.user_service.controller.mentorship;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MentorshipService {
    private final UserRepository userRepository;

    @Autowired
    public MentorshipService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    public List<User> getMentees(long userId){
        List<User> userMentees = new ArrayList<User>();

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new RuntimeException("User not found");
        }
        Optional<List<User>> mentees = Optional.ofNullable(user.get().getMentees());
        mentees.ifPresent(userMentees::addAll);
        return userMentees;
    }
}
