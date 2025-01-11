package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.global.BadRequestException;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    private static final Long USER_ID = 10L;
    private static final Long MENTEE_ID = 1L;
    private static final Long MENTOR_ID = 3L;
    private static final Long NOT_EXISTING_ID = 20L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User user;
    private User mentor;
    private User otherMentor;

    @BeforeEach
    void init() {
        User user1 = User.builder()
                .id(1L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .build();
        User user3 = User.builder()
                .id(3L)
                .build();
        User user4 = User.builder()
                .id(4L)
                .build();
        User user5 = User.builder()
                .id(5L)
                .build();

        user = User.builder()
                .mentees(List.of(user1, user2))
                .mentors(List.of(user3, user4, user5))
                .build();

        mentor = new User();
        mentor.setId(1L);

        otherMentor = new User();
        otherMentor.setId(2L);

        List<Goal> goals = new ArrayList<>(List.of(
                Goal.builder()
                        .mentor(mentor)
                        .build(),
                Goal.builder()
                        .mentor(otherMentor)
                        .build()
        ));

        mentor.setMentees(new ArrayList<>(List.of(
                User.builder()
                        .id(2L)
                        .mentors(new ArrayList<>(List.of(mentor)))
                        .goals(goals)
                        .build(),
                User.builder()
                        .id(3L)
                        .mentors(new ArrayList<>(List.of(mentor, otherMentor)))
                        .goals(new ArrayList<>(List.of(goals.get(1))))
                        .build()
        )));
    }

    @Test
    void testGetMentees() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.ofNullable(user));

        var result = mentorshipService.getMentees(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);

        var mentees = user.getMentees();
        assertTrue(result.containsAll(mentees));
        assertTrue(mentees.containsAll(result));
    }

    @Test
    void testGetMenteesThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        var result = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.getMentees(USER_ID)
        );

        assertEquals(new UserNotFoundException(10L).getMessage(), result.getMessage());
    }

    @Test
    void testGetMentors() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.ofNullable(user));

        var result = mentorshipService.getMentors(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);

        var mentors = user.getMentors();
        assertTrue(mentors.containsAll(result));
        assertTrue(result.containsAll(mentors));
    }

    @Test
    void testGetMentorsThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        var result = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.getMentors(USER_ID)
        );

        assertEquals(new UserNotFoundException(10L).getMessage(), result.getMessage());
    }

    @Test
    void deleteMentee() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.ofNullable(user));

        mentorshipService.deleteMentee(MENTEE_ID, USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(mentorshipRepository, times(1)).delete(MENTEE_ID, USER_ID);
    }

    @Test
    void testDeleteMenteeThrowsUserNotFoundExceptionIfUserNotFound() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        var result = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentee(MENTEE_ID, USER_ID)
        );

        assertEquals(new UserNotFoundException(USER_ID).getMessage(), result.getMessage());
    }

    @Test
    void testDeleteMenteeThrowsUserNotFoundExceptionIfMenteeNotFoundInListOfMentees() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.ofNullable(user));

        var result = assertThrows(
                BadRequestException.class,
                () -> mentorshipService.deleteMentee(NOT_EXISTING_ID, USER_ID)
        );

        assertEquals(
                String.format("User with id = [%d] is not a mentee of user with id = [%d].",
                        NOT_EXISTING_ID, USER_ID),
                result.getMessage()
        );
    }

    @Test
    void deleteMentor() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.ofNullable(user));

        mentorshipService.deleteMentor(USER_ID, MENTOR_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(mentorshipRepository, times(1)).delete(USER_ID, MENTOR_ID);
    }

    @Test
    void testDeleteMentorThrowsUserNotFoundExceptionIfUserNotFound() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        var result = assertThrows(
                UserNotFoundException.class,
                () -> mentorshipService.deleteMentor(USER_ID, MENTOR_ID)
        );

        assertEquals(new UserNotFoundException(USER_ID).getMessage(), result.getMessage());
    }

    @Test
    void testDeleteMentorThrowsUserNotFoundExceptionIfMentorNotFoundInListOfMentors() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.ofNullable(user));

        var result = assertThrows(
                BadRequestException.class,
                () -> mentorshipService.deleteMentor(USER_ID, NOT_EXISTING_ID)
        );

        assertEquals(
                String.format("User with id = [%d] is not a mentor of user with id = [%d].",
                        NOT_EXISTING_ID, USER_ID),
                result.getMessage()
        );
    }

    @Test
    public void deleteMentorFromMenteesSuccess() {
        List<User> mentees = mentor.getMentees();

        mentorshipService.deleteMentorFromMentees(mentor.getId(), mentees);

        verify(mentorshipRepository).saveAll(mentor.getMentees());

        User mentee1 = mentees.get(0);
        User mentee2 = mentees.get(1);

        assertEquals(mentee1.getGoals().get(0).getMentor(), mentee1);
        assertEquals(mentee1.getGoals().get(1).getMentor(), otherMentor);
        assertEquals(mentee1.getMentors().size(), 0);

        assertEquals(mentee2.getGoals().get(0).getMentor(), otherMentor);
        assertEquals(mentee2.getMentors().size(), 1);
    }
}