package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserDto> getMentees(long id) {
        User mentor = getUserById(id);
        if (!CollectionUtils.isEmpty(mentor.getMentees())) {
            return mentor.getMentees().stream().map(mapper::toDto).toList();
        } else {
            return Collections.emptyList();
        }
    }

    public List<UserDto> getMentors(long id) {
        User mentee = getUserById(id);
        if (!CollectionUtils.isEmpty(mentee.getMentors())) {
            return mentee.getMentors().stream().map(mapper::toDto).toList();
        } else {
            return Collections.emptyList();
        }
    }

    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = getUserById(mentorId);
        mentor.setMentees(
                mentor.getMentees().stream().filter(mentee -> mentee.getId() != menteeId).toList()
        );
        repository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getUserById(menteeId);
        mentee.setMentors(
                mentee.getMentors().stream().filter(mentor -> mentor.getId() != mentorId).toList()
        );
        repository.save(mentee);
    }

    private User getUserById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователя с таким id не существует"));
    }
}
