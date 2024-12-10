package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.RetryProperties;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.UserProfileDeactivatedEvent;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final RetryProperties retryProperties;
    private final UserMapper userMapper;
    private final UserService userService;
    private final UserValidator userValidator;

    public List<UserDto> getMentees(long userId) {
        User user = userService.findUserById(userId);
        return userMapper.toDto(user.getMentees());
    }

    public List<UserDto> getMentors(long userId) {
        User user = userService.findUserById(userId);
        return userMapper.toDto(user.getMentors());
    }

    @Transactional
    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = userService.findUserById(mentorId);
        boolean isRemoved = mentor.getMentees().removeIf(mentee -> mentee.getId().equals(menteeId));
        if (isRemoved) {
            userService.saveUser(mentor);
        } else {
            log.info("Mentor " + mentor.getUsername() + " does not have a mentee with "
                    + menteeId + " id");
        }
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userService.findUserById(menteeId);
        boolean isRemoved = mentee.getMentors().removeIf(mentor -> mentor.getId().equals(mentorId));
        if (isRemoved) {
            userService.saveUser(mentee);
        } else {
            log.info("User " + mentee.getUsername()
                    + " does not have a mentor with " + mentorId + " id");
        }
    }

    @EventListener
    @Retryable(retryFor = Exception.class,
            maxAttemptsExpression = "#{@retryProperties.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@retryProperties.initialDelay}",
                    multiplierExpression = "#{@retryProperties.multiplier}",
                    maxDelayExpression = "#{@retryProperties.maxDelay}"
            )
    )
    public void handleUserProfileDeactivatedEvent(UserProfileDeactivatedEvent event) {
        User mentor = userService.findUserById(event.getUserId());
        if (userValidator.isUserMentor(mentor)) {
            mentor.getMentees().forEach(mentee -> {
                moveGoalsToMentee(mentor, mentee.getId());
                deleteMentor(mentee.getId(), mentor.getId());
            });
        }
        log.info("Goals of a mentor with id {} were moved to mentees", event.getUserId());
        log.info("Mentorship with id {} was deleted", event.getUserId());
    }

    private void moveGoalsToMentee(User mentor, long menteeId) {
        User mentee = userService.findUserById(menteeId);
        mentor.getSetGoals()
                .forEach(goal -> {
                    if (goal.getUsers().contains(mentee)) {
                        goal.setMentor(mentee);
                    }
                });
    }
}

