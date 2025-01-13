package school.faang.user_service.service.mentorship;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserDto> getMentees(long id) {
        User mentor = getUserByIdFromRepo(id);
        if (mentor.getMentees() != null) {
            return mentor.getMentees().stream().map(mapper::toDto).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<UserDto> getMentors(long id) {
        User mentee = getUserByIdFromRepo(id);
        if (mentee.getMentors() != null) {
            return mentee.getMentors().stream().map(mapper::toDto).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = getUserByIdFromRepo(mentorId);
        mentor.setMentees(
                mentor.getMentees().stream().filter(mentee -> mentee.getId() != menteeId).toList()
        );
        repository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getUserByIdFromRepo(menteeId);
        mentee.setMentors(
                mentee.getMentors().stream().filter(mentor -> mentor.getId() != mentorId).toList()
        );
        repository.save(mentee);
    }

    private User getUserByIdFromRepo(long id) {
        Optional<User> userOpt = repository.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователя с таким id не существует");
        }
        return userOpt.get();
    }
}
