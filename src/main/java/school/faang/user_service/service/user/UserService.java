package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.entity.AvatarStyle;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.user.UserDeactivatedException;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.event.EventDomainService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.time.LocalDateTime;
import java.util.List;

import static school.faang.user_service.enums.publisher.PublisherType.PROFILE_VIEW;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserValidationService userValidationService;
    private final EventDomainService eventDomainService;
    private final UserDomainService userDomainService;
    private final MentorshipService mentorshipService;
    private final AvatarService avatarService;
    private final GoalService goalService;

    @Transactional
    public User registerUser(User user) {
        userValidationService.validateUsernameAndEmail(user);
        UserProfilePic userProfilePic = avatarService.generateAndSaveAvatar(AvatarStyle.BOTTTTS);
        user.setUserProfilePic(userProfilePic);
        user.setCreatedAt(LocalDateTime.now());

        return userDomainService.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userDomainService.findById(userId);

        if (!user.isActive()) {
            throw new UserDeactivatedException(userId);
        }

        removeUserGoals(user);
        removeUserEvents(user);
        mentorshipService.deleteMentorFromMentees(user.getId(), user.getMentees());

        user.setActive(false);
        userDomainService.save(user);
    }

    @Transactional
    public void bannedUser(Long userId) {
        User user = userDomainService.findById(userId);
        user.setBanned(true);
        log.info("User banned: {}", userId);
        userDomainService.save(user);
    }

    @PublishEvent(type = PROFILE_VIEW)
    @Transactional(readOnly = true)
    public List<User> getPremiumUsers(long offset, long limit) {
        return userDomainService.findAllWithActivePremiumInRange(offset, limit);
    }

    private void removeUserGoals(User user) {
        user.getGoals().forEach(goal -> {
            if (goal.getUsers().size() == 1) {
                goalService.deleteGoalAndUnlinkChildren(goal);
            }
            goal.getUsers().remove(user);
        });
        user.getGoals().clear();
    }

    private void removeUserEvents(User user) {
        if (user.getOwnedEvents() == null) {
            return;
        }
        List<Event> plannedEvents = user.getOwnedEvents()
                .stream()
                .filter(event -> event.getStatus().equals(EventStatus.PLANNED))
                .toList();

        plannedEvents.forEach(event -> {
            event.setStatus(EventStatus.CANCELED);
            eventDomainService.save(event);
            eventDomainService.delete(event);
        });
    }
}
