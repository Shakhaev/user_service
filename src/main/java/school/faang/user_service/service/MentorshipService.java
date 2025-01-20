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
    private final UserService service;
    private final UserMapper mapper;

    public List<UserDto> getMentees(long id) {
        User mentor = service.getUser(id);
        if (!CollectionUtils.isEmpty(mentor.getMentees())) {
            return mentor.getMentees().stream().map(mapper::toDto).toList();
        } else {
            return Collections.emptyList();
        }
    }

    public List<UserDto> getMentors(long id) {
        User mentee = service.getUser(id);
        if (!CollectionUtils.isEmpty(mentee.getMentors())) {
            return mentee.getMentors().stream().map(mapper::toDto).toList();
        } else {
            return Collections.emptyList();
        }
    }

    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = service.getUser(mentorId);
        mentor.setMentees(
                mentor.getMentees().stream().filter(mentee -> mentee.getId() != menteeId).toList()
        );
        repository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = service.getUser(menteeId);
        mentee.setMentors(
                mentee.getMentors().stream().filter(mentor -> mentor.getId() != mentorId).toList()
        );
        repository.save(mentee);
    }

    public void removeMentorship(long mentorId) {
        User mentor = service.getUser(mentorId);
        mentor.getMentees().forEach(mentee -> {
            deleteMentor(mentee.getId(), mentorId);
            mentee.setGoals(mentee.getGoals().stream().map(goal -> {
                if (goal.getMentor().getId() == mentorId) {
                    goal.setMentor(null);
                }
                return goal;
            }).toList());
            repository.save(mentee);
        });
        mentor.setMentees(Collections.emptyList());
        repository.save(mentor);
    }
}
