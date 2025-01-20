package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public List<UserDto> getMentees(long id) {
        User mentor = getUser(id);
        if (!CollectionUtils.isEmpty(mentor.getMentees())) {
            return mentor.getMentees().stream().map(mapper::toDto).toList();
        } else {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(long id) {
        User mentee = getUser(id);
        if (!CollectionUtils.isEmpty(mentee.getMentors())) {
            return mentee.getMentors().stream().map(mapper::toDto).toList();
        } else {
            return Collections.emptyList();
        }
    }

    @Transactional
    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = getUser(mentorId);
        mentor.setMentees(
                mentor.getMentees().stream().filter(mentee -> mentee.getId() != menteeId).toList()
        );
        userRepository.save(mentor);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getUser(menteeId);
        mentee.setMentors(
                mentee.getMentors().stream().filter(mentor -> mentor.getId() != mentorId).toList()
        );
        userRepository.save(mentee);
    }

    @Transactional
    public void removeMentorship(long mentorId) {
        User mentor = getUser(mentorId);
        mentor.getMentees().forEach(mentee -> {
            deleteMentor(mentee.getId(), mentorId);
            mentee.setGoals(mentee.getGoals().stream().map(goal -> {
                if (goal.getMentor().getId() == mentorId) {
                    goal.setMentor(null);
                }
                return goal;
            }).toList());
            userRepository.save(mentee);
        });
        mentor.setMentees(Collections.emptyList());
        userRepository.save(mentor);
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Не удалось получить пользователя с id " + id));
    }
}
