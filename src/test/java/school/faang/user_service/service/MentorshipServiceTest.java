package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private User user;

    @Mock
    private User mentee1;

    @Mock
    private User mentee2;

    private List<User> mentee1Mentors;
    private List<User> mentee2Mentors;
    private Goal goal1;
    private Goal goal2;

    @BeforeEach
    void setUp() {
        mentee1Mentors = new ArrayList<>(List.of(user));
        mentee2Mentors = new ArrayList<>(List.of(user));

        goal1 = Goal.builder()
                .id(1L)
                .title("Goal 1")
                .description("Description 1")
                .mentor(user)
                .build();

        goal2 = Goal.builder()
                .id(2L)
                .title("Goal 2")
                .description("Description 2")
                .mentor(user)
                .build();
    }

    @Test
    void removeMenteeFromUser_ShouldRemoveMentorFromMentees() {

        when(userService.getById(USER_ID)).thenReturn(user);
        when(user.getMentees()).thenReturn(List.of(mentee1, mentee2));
        when(mentee1.getMentors()).thenReturn(mentee1Mentors);
        when(mentee2.getMentors()).thenReturn(mentee2Mentors);

        mentorshipService.removeMenteeFromUser(USER_ID);

        assertFalse(mentee1Mentors.contains(user));
        assertFalse(mentee2Mentors.contains(user));

        verify(userService).getById(USER_ID);
        verify(user).getMentees();
        verify(userService).saveUser(mentee1);
        verify(userService).saveUser(mentee2);
    }

    @Test
    void shouldRemoveMenteeGoals() {
        List<Goal> mentee1Goals = List.of(goal1);
        List<Goal> mentee2Goals = List.of(goal2);

        when(user.getId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);
        when(user.getMentees()).thenReturn(List.of(mentee1, mentee2));
        when(mentee1.getSetGoals()).thenReturn(mentee1Goals);
        when(mentee2.getSetGoals()).thenReturn(mentee2Goals);

        mentorshipService.removeMenteeGoals(USER_ID);

        verify(goalRepository, times(1)).saveAll(anyList());
        assertEquals(goal1.getMentor(), mentee1);
        assertEquals(goal2.getMentor(), mentee2);
    }
}