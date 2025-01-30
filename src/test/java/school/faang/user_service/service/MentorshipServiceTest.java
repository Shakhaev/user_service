package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MenteeReadDto;
import school.faang.user_service.dto.mentorship.MentorReadDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.MenteeReadMapper;
import school.faang.user_service.mapper.mentorship.MentorReadMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    private static final Long FIRST_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;

    private static final Long USER_ID = 1L;
    private static final List<Long> USERS_ID_LIST = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MenteeReadMapper menteeReadMapper;

    @Mock
    private UserService userService;

    @Mock
    private MentorReadMapper mentorReadMapper;

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
    private List<Goal> mentee1Goals;
    private List<Goal> mentee2Goals;
    private Goal goal1;
    private Goal goal2;

    @BeforeEach
    void setUp() {
        mentee1Mentors = new ArrayList<>(List.of(user));
        mentee2Mentors = new ArrayList<>(List.of(user));

        goal1 = Goal.builder()
                .id(FIRST_USER_ID)
                .title("Цель 1")
                .description("Описание 1")
                .mentor(user)
                .build();

        goal2 = Goal.builder()
                .id(SECOND_USER_ID)
                .title("Цель 2")
                .description("Описание 2")
                .mentor(user)
                .build();
        mentee1Goals = List.of(goal1);
        mentee2Goals = List.of(goal2);
        }


    @Test
    void testGetMenteesMethodWhenMentorHasMentees() {
        User mentor = new User();
        mentor.setId(USER_ID);

        List<User> mentees = new ArrayList<>();
        User mentee;

        for (Long menteeId : USERS_ID_LIST) {
            mentee = new User();
            mentee.setId(menteeId);
            mentees.add(mentee);
        }

        mentor.setMentees(mentees);

        Mockito.when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(mentor));
        Mockito.when(menteeReadMapper.toDto(any(User.class))).thenReturn(new MenteeReadDto());
        List<MenteeReadDto> menteesDto = mentorshipService.getMentees(USER_ID);
        Mockito.verify(menteeReadMapper, Mockito.times(9)).toDto(any(User.class));
        Assertions.assertNotNull(menteesDto);
        Assertions.assertEquals(USERS_ID_LIST.size(), menteesDto.size());
    }

    @Test
    void testGetMenteesMethodWhenMentorHasNotMentees() {
        User mentor = new User();
        mentor.setId(USER_ID);
        mentor.setMentees(List.of());

        Mockito.when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(mentor));
        List<MenteeReadDto> mentees = mentorshipService.getMentees(USER_ID);
        Assertions.assertTrue(mentees.isEmpty());
    }

    @Test
    void testGetMenteesMethodWhenMentorDoesNotExist() {
        Mockito.when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(USER_ID));
        Assertions.assertEquals("Пользователь с ID " + USER_ID + " не найден", exception.getMessage());
    }

  @Test
    void testGetMentorsMethodWhenMenteeHasMentors() {
        User mentee = new User();
        mentee.setId(USER_ID);

        List<User> mentors = new ArrayList<>();
        User mentor;

        for (Long mentorId : USERS_ID_LIST) {
            mentor = new User();
            mentor.setId(mentorId);
            mentors.add(mentor);
        }

        mentee.setMentors(mentors);

        Mockito.when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(mentee));
        Mockito.when(mentorReadMapper.toDto(any(User.class))).thenReturn(new MentorReadDto());

        List<MentorReadDto> mentorsDto = mentorshipService.getMentors(USER_ID);

        Mockito.verify(mentorReadMapper, Mockito.times(9)).toDto(any(User.class));
        Assertions.assertNotNull(mentorsDto);
        Assertions.assertEquals(USERS_ID_LIST.size(), mentorsDto.size());
    }

   @Test
    void testGetMentorsMethodWhenMenteeHasNotMentors() {
        User mentee = new User();
        mentee.setId(USER_ID);
        mentee.setMentors(List.of());

        Mockito.when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.of(mentee));

        List<MentorReadDto> mentors = mentorshipService.getMentors(USER_ID);
        Assertions.assertTrue(mentors.isEmpty());
    }

    @Test
    void testGetMentorsMethodWhenMenteeDoesNotExist() {
        Mockito.when(mentorshipRepository.findById(USER_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(USER_ID));
        Assertions.assertEquals("Пользователь с ID " + USER_ID + " не найден", exception.getMessage());
    }

    @Test
    void testDeleteMenteeMethodWhenMentorHasMentees() {
        User mentee = new User();
        mentee.setId(USER_ID);

        User mentor = new User();
        mentor.setId(USER_ID + 1);

        List<User> mentees = new ArrayList<>();
        mentees.add(mentee);
        User user;

        for (Long menteeId : USERS_ID_LIST) {
            if (menteeId.equals(USER_ID) || menteeId.equals(USER_ID + 1)) {
                continue;
            }
            user = new User();
            user.setId(menteeId);
            mentees.add(user);
        }

        mentor.setMentees(mentees);

        Mockito.when(mentorshipRepository.findById(USER_ID + 1)).thenReturn(Optional.of(mentor));
        mentorshipService.deleteMentee(USER_ID, USER_ID + 1);
        Assertions.assertFalse(mentor.getMentees().contains(mentee));
        Mockito.verify(mentorshipRepository).save(mentor);
    }

    @Test
    void testDeleteMenteeMethodWhenMentorHasNotMentees() {
        User mentee = new User();
        mentee.setId(USER_ID);

        User mentor = new User();
        mentor.setId(USER_ID + 1);
        mentor.setMentees(new ArrayList<>());

        Mockito.when(mentorshipRepository.findById(USER_ID + 1)).thenReturn(Optional.of(mentor));
        mentorshipService.deleteMentee(USER_ID, USER_ID + 1);
        Mockito.verify(mentorshipRepository, Mockito.never()).save(mentor);
    }

    @Test
    void testDeleteMenteeMethodWhenMentorDoesNotExist() {
        User mentee = new User();
        mentee.setId(USER_ID);

        Mockito.when(mentorshipRepository.findById(USER_ID + 1)).thenReturn(Optional.empty());
        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(USER_ID, USER_ID + 1)
        );
        Assertions.assertEquals("Пользователь с ID " + (USER_ID + 1) + " не найден", exception.getMessage());
    }

    @Test
    void testDeleteMentorMethodWhenMenteeHasMentors() {
        User mentor = new User();
        mentor.setId(USER_ID);

        User mentee = new User();
        mentee.setId(USER_ID + 1);

        List<User> mentors = new ArrayList<>();
        mentors.add(mentor);
        User user;

        for (Long mentorId : USERS_ID_LIST) {
            if (mentorId.equals(USER_ID) || mentorId.equals(USER_ID + 1)) {
                continue;
            }
            user = new User();
            user.setId(mentorId);
            mentors.add(user);
        }

        mentee.setMentors(mentors);

        Mockito.when(mentorshipRepository.findById(USER_ID + 1)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentor(USER_ID, USER_ID + 1);
        Assertions.assertFalse(mentee.getMentors().contains(mentor));
        Mockito.verify(mentorshipRepository).save(mentee);
    }

    @Test
    void testDeleteMentorMethodWhenMenteeHasNotMentors() {
        User mentor = new User();
        mentor.setId(USER_ID);

        User mentee = new User();
        mentee.setId(USER_ID + 1);
        mentee.setMentors(new ArrayList<>());

        Mockito.when(mentorshipRepository.findById(USER_ID + 1)).thenReturn(Optional.of(mentee));
        mentorshipService.deleteMentor(USER_ID, USER_ID + 1);
        Mockito.verify(mentorshipRepository, Mockito.never()).save(mentee);
    }

    @Test
    void testDeleteMentorMethodWhenMenteeDoesNotExist() {
        User mentor = new User();
        mentor.setId(USER_ID);

        Mockito.when(mentorshipRepository.findById(USER_ID + 1)).thenReturn(Optional.empty());
        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipService.deleteMentor(USER_ID, USER_ID + 1)
        );
        Assertions.assertEquals("Пользователь с ID " + (USER_ID + 1) + " не найден", exception.getMessage());
    }

    @Test
    void removeMenteeFromUser_ShouldRemoveMentorFromMentees() {

        when(userService.getById(FIRST_USER_ID)).thenReturn(user);
        when(user.getMentees()).thenReturn(List.of(mentee1, mentee2));
        when(mentee1.getMentors()).thenReturn(mentee1Mentors);
        when(mentee2.getMentors()).thenReturn(mentee2Mentors);

        mentorshipService.removeMenteeFromUser(FIRST_USER_ID);

        assertFalse(mentee1Mentors.contains(user));
        assertFalse(mentee2Mentors.contains(user));

        verify(userService).getById(FIRST_USER_ID);
        verify(user).getMentees();
        verify(userService).saveUser(mentee1);
        verify(userService).saveUser(mentee2);
    }

    @Test
    void shouldRemoveMenteeGoals() {

        when(user.getId()).thenReturn(FIRST_USER_ID);
        when(userService.getById(FIRST_USER_ID)).thenReturn(user);
        when(user.getMentees()).thenReturn(List.of(mentee1, mentee2));
        when(mentee1.getSetGoals()).thenReturn(mentee1Goals);
        when(mentee2.getSetGoals()).thenReturn(mentee2Goals);

        mentorshipService.removeMenteeGoals(FIRST_USER_ID);

        verify(goalRepository, times(1)).saveAll(anyList());
        assertEquals(goal1.getMentor(), mentee1);
        assertEquals(goal2.getMentor(), mentee2);
    }
}