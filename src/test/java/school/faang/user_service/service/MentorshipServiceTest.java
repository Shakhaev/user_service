package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.event.UserProfileDeactivatedEvent;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @InjectMocks
    public MentorshipService mentorshipService;
    @Mock
    private UserService userService;
    @Mock
    private UserValidator userValidator;
    @Spy
    private UserMapperImpl userMapper;

    private User mentor;
    private User mentee;
    private User mentee2;
    private UserProfileDeactivatedEvent event;

    @BeforeEach
    public void setUp() {
        mentor = new User();
        mentor.setId(1L);
        mentee = new User();
        mentee.setId(2L);
        mentee2 = new User();
        mentee2.setId(3L);

        Goal goalWithMentee = new Goal();
        goalWithMentee.setUsers(List.of(mentee));
        Goal goalWithoutMentee = new Goal();
        goalWithoutMentee.setUsers(new ArrayList<>());

        mentor.setSetGoals(List.of(goalWithMentee, goalWithoutMentee));
    }

    @Test
    void getMenteesWhenUserHasMentees() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        User mentee1 = new User();
        mentee1.setId(2L);
        List<User> mentees = List.of(mentee1, mentee2);
        user.setMentees(mentees);
        when(userService.findUserById(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void getMenteesUserWithNoMenteesReturnsEmptyList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMentees(Collections.emptyList());
        when(userService.findUserById(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void testGetMentorsWhenUserHasMentees() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        User mentor1 = new User();
        mentor1.setId(2L);
        User mentor2 = new User();
        mentor2.setId(3L);
        List<User> mentors = List.of(mentor1, mentor2);
        user.setMentors(mentors);
        when(userService.findUserById(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void testGetMentorsUserWithNoMenteesReturnsEmptyList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMentors(Collections.emptyList());
        when(userService.findUserById(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentors(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void testDeleteMenteeWhenMenteeExists() {
        User mentor = new User();
        mentor.setId(1L);
        User mentee = new User();
        mentee.setId(2L);
        mentor.setMentees(new ArrayList<>());
        mentor.getMentees().add(mentee);
        when(userService.findUserById(mentor.getId())).thenReturn(mentor);

        mentorshipService.deleteMentee( mentor.getId(),mentee.getId());

        verify(userService, times(1)).saveUser(mentor);
        assertTrue(mentor.getMentees().isEmpty());
    }

    @Test
    void testDeleteMenteeWhenMenteeDoesNotExist() {
        User mentor = new User();
        mentor.setId(1L);
        mentor.setMentees(new ArrayList<>());
        when(userService.findUserById(mentor.getId())).thenReturn(mentor);

        mentorshipService.deleteMentee( mentor.getId(), mentee2.getId());

        verify(userService, never()).saveUser(mentor);
    }

    @Test
    void testDeleteMentorWhenMenteeExists() {
        User mentee = new User();
        mentee.setId(1L);
        User mentor = new User();
        mentor.setId(2L);
        mentee.setMentors(new ArrayList<>());
        mentee.getMentors().add(mentor);
        when(userService.findUserById(mentee.getId())).thenReturn(mentee);

        mentorshipService.deleteMentor(mentee.getId(), mentor.getId());

        verify(userService, times(1)).saveUser(mentee);
        assertTrue(mentee.getMentors().isEmpty());
    }

    @Test
    void testDeleteMentorWhenMenteeDoesNotExist() {
        User mentee = new User();
        mentee.setId(1L);
        mentee.setMentors(new ArrayList<>());
        when(userService.findUserById(mentee.getId())).thenReturn(mentee);

        mentorshipService.deleteMentor(mentee.getId(), 3L);

        verify(userService, never()).saveUser(mentee);
    }

    @Test
    @DisplayName("Handle User Profile Deactivated Event success")
    void testHandleUserProfileDeactivatedEvent() {
        event = new UserProfileDeactivatedEvent(this, mentor.getId());

        List<User> mentees = List.of(mentee, mentee2);
        mentor.setMentees(mentees);
        mentee.setMentors(new ArrayList<>(List.of(mentor)));
        mentee2.setMentors(new ArrayList<>(List.of(mentor)));

        when(userService.findUserById(mentor.getId())).thenReturn(mentor);
        when(userService.findUserById(mentee.getId())).thenReturn(mentee);
        when(userService.findUserById(mentee2.getId())).thenReturn(mentee2);
        when(userValidator.isUserMentor(mentor)).thenReturn(true);

        mentorshipService.handleUserProfileDeactivatedEvent(event);

        assertEquals(mentee, mentor.getSetGoals().stream()
                .filter(goal -> goal.getUsers().contains(mentee))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Goal not found"))
                .getMentor());

        verify(userService, times(1)).findUserById(mentor.getId());
        verify(userService, times(2)).findUserById(mentee.getId());
        verify(userService, times(2)).findUserById(mentee2.getId());
        verify(userValidator, times(1)).isUserMentor(mentor);
        verify(userService, times(1)).saveUser(mentee);
    }

    @Test
    @DisplayName("Handle User Profile Deactivated Event success: User is not a mentor")
    void testHandleUserProfileDeactivatedEvent_NotMentor_Success() {
        event = new UserProfileDeactivatedEvent(this, mentor.getId());
        when(userService.findUserById(mentor.getId())).thenReturn(mentor);
        when(userValidator.isUserMentor(mentor)).thenReturn(false);

        mentorshipService.handleUserProfileDeactivatedEvent(event);

        verify(userService, times(1)).findUserById(mentor.getId());
        verify(userValidator, times(1)).isUserMentor(mentor);
    }

}


