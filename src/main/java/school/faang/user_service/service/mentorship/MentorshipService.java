package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public List<User> getMentees(long id) {
        UserDto mentorDto = repository.getUserById(id);
        User mentor = mapper.toEntity(mentorDto);

        if (mentor.getMentees() != null) {
            return mentor.getMentees();
        } else {
            return new ArrayList<>();
        }
    }

    public List<User> getMentors(long id) {
        UserDto menteeDto = repository.getUserById(id);
        User mentee = mapper.toEntity(menteeDto);

        if (mentee.getMentors() != null) {
            return mentee.getMentors();
        } else {
            return new ArrayList<>();
        }
    }

    public void deleteMentee(long mentorId, long menteeId) {
        UserDto mentorDto = repository.getUserById(mentorId);
        User mentor = mapper.toEntity(mentorDto);

        mentor.setMentees(
                mentor.getMentees().stream().filter(mentee -> mentee.getId() == menteeId).toList()
        );

        repository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        UserDto menteeDto = repository.getUserById(menteeId);
        User mentee = mapper.toEntity(menteeDto);

        mentee.setMentees(
                mentee.getMentees().stream().filter(mentor -> mentor.getId() == mentorId).toList()
        );

        repository.save(mentee);
    }
}
