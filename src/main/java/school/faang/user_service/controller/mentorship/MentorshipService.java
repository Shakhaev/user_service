package school.faang.user_service.controller.mentorship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
public class MentorshipService {
    private final UserRepository userRepository;

    @Autowired
    public MentorshipService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void getMentees(long userId){


        //Распаковка optional

        //user.getMentees
        //завести DTO UserDTOResponse
        //ID, Name
        //Mapper(MapStruct) -> UserDTOResponse

    }
}
