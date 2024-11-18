package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserMapper userMapper;
    private final UserService userService;

    public List<UserDto> getMentees(long userId) {
        User user = userService.findUserById(userId);
        return userMapper.toDto(user.getMentees());
    }

    public List<UserDto> getMentors(long userId) {
        User user = userService.findUserById(userId);
        return userMapper.toDto(user.getMentors());
    }

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userService.findUserById(mentorId);
        boolean remove = mentor.getMentees().removeIf(mentee -> mentee.getId().equals(menteeId));
        if (remove) {
            userService.saveUser(mentor);
        } else {
            log.info("Mentor " + mentor.getUsername() + " does not have a mentee with "
                    + menteeId + " id");
        }
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userService.findUserById(menteeId);
        boolean remove = mentee.getMentors().removeIf(mentor -> mentor.getId().equals(mentorId));
        if (remove) {
            userService.saveUser(mentee);
        } else {
            log.info("User " + mentee.getUsername()
                    + " does not have a mentor with " + mentorId + " id");
        }
    }

    @Transactional
    public void moveGoalsToMentee(long menteeId, long mentorId) {
        User mentor = userService.findUserById(mentorId);
        User mentee = userService.findUserById(menteeId);
        mentor.getSetGoals()
                .forEach(goal -> {
                    if (goal.getUsers().contains(mentee)) {
                        goal.setMentor(mentee);
                    }
                });
    }

}

