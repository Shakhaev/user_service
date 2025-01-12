package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.AvatarStyle;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.user.UserDeactivatedException;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.event.EventDomainService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class
UserServiceTest extends AbstractUserServiceTest {
    @Mock
    private UserValidationService userValidationService;
    @Mock
    private EventDomainService eventDomainService;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private UserDomainService userDomainService;
    @Mock
    private AvatarService avatarService;
    @Mock
    private GoalService goalService;
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setActive(true);

        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Goal 1");
        goal1.setUsers(new ArrayList<>(List.of(user)));

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Goal 2");
        goal2.setUsers(new ArrayList<>(List.of(user, new User())));
        user.setGoals(new ArrayList<>(List.of(goal1, goal2)));

        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Webinar Java");
        event1.setStatus(EventStatus.PLANNED);
        event1.setOwner(user);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Webinar Spring");
        event2.setStatus(EventStatus.COMPLETED);
        event2.setOwner(user);
        user.setOwnedEvents(new ArrayList<>(List.of(event1, event2)));
    }

    @Test
    public void deactivateUserSuccess() {
        Long userId = 1L;
        when(userDomainService.findById(userId)).thenReturn(user);
        List<Goal> userGoals = new ArrayList<>(user.getGoals());
        List<Event> userEvents = new ArrayList<>(user.getOwnedEvents());

        userService.deactivateUser(userId);

        verify(userDomainService).findById(userId);
        verify(goalService).deleteGoalAndUnlinkChildren(userGoals.get(0));
        verify(eventDomainService).save(userEvents.get(0));
        verify(eventDomainService).delete(userEvents.get(0));
        verify(mentorshipService).deleteMentorFromMentees(userId, user.getMentees());
        verify(userDomainService).save(user);

        assertEquals(user.getGoals().size(), 0);
    }

    @Test
    public void deactivateUserFailed() {
        Long userId = 1L;
        user.setActive(false);

        when(userDomainService.findById(userId)).thenReturn(user);

        assertThrows(UserDeactivatedException.class, () -> userService.deactivateUser(userId));
    }

    @Test
    public void testRegisterUser() {
        User user = new User();
        user.setUsername("testuser");

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("avatar.png");
        userProfilePic.setSmallFileId("avatar_small.png");

        when(avatarService.generateAndSaveAvatar(AvatarStyle.BOTTTTS)).thenReturn(userProfilePic);
        when(userDomainService.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNotNull(result.getUserProfilePic());
        assertEquals("avatar.png", result.getUserProfilePic().getFileId());

        verify(userValidationService).validateUsernameAndEmail(user);
        verify(userDomainService, times(1)).save(user);
        verify(avatarService, times(1)).generateAndSaveAvatar(AvatarStyle.BOTTTTS);
    }

    @Test
    void testGetPremiumUsers() {
        long offset = 0;
        long limit = 2;
        List<User> users = List.of(user);

        when(userDomainService.findAllWithActivePremiumInRange(offset, limit)).thenReturn(users);

        assertThat(userService.getPremiumUsers(offset, limit).get(0))
                .isInstanceOf(User.class);
    }

    @Test
    void testBannedUser() {
        long userId = 1L;
        when(userDomainService.findById(userId)).thenReturn(user);

        userService.bannedUser(userId);

        Assertions.assertTrue(user.isBanned());
        verify(userDomainService).save(user);
    }
}
