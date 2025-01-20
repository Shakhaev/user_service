package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class MentorshipServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    private MentorshipService mentorshipService;

    private User mentor;
    private User mentee;
    private Goal goal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mentorshipService = new MentorshipService(goalRepository, userRepository);

        mentor = new User();
        mentor.setId(1L);
        mentor.setMentees(new ArrayList<>());
        mentor.setGoals(new ArrayList<>());

        mentee = new User();
        mentee.setId(2L);
        mentee.setMentors(new ArrayList<>());
        mentee.setGoals(new ArrayList<>());

        goal = new Goal();
        goal.setId(1L);
        goal.setMentor(mentor);

        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);
        mentor.getGoals().add(goal);
    }


    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Long invalidUserId = 999L;
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                mentorshipService.deactivateMentorship(invalidUserId)
        );

        assertEquals("Пользователь с ID 999 не найден", exception.getMessage());
    }

    @Test
    void shouldDeactivateMentorship() {
        Long validUserId = 1L;
        when(userRepository.findById(validUserId)).thenReturn(Optional.of(mentor));

        mentorshipService.deactivateMentorship(validUserId);

        assertTrue(mentee.getMentors().isEmpty());

        assertNull(goal.getMentor());

        assertTrue(mentee.getGoals().contains(goal));

        verify(goalRepository, times(1)).save(goal);
        verify(userRepository, times(1)).save(mentor);
    }

    @Test
    void shouldRemoveMentorFromMentees() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(userRepository.save(any(User.class))).thenReturn(mentor);

        mentorshipService.deactivateMentorship(mentor.getId());

        assertTrue(mentee.getMentors().isEmpty());
    }

    @Test
    void shouldReassignGoals() {
        when(userRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(userRepository.save(any(User.class))).thenReturn(mentor);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        mentorshipService.deactivateMentorship(mentor.getId());

        assertTrue(mentee.getGoals().contains(goal));
    }

}
