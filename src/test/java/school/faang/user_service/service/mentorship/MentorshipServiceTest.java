package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;
    private MentorshipValidator mentorshipValidator;
    private User mentor = new User();
    private User mentee = new User();

    private long mentorId;
    private long menteeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMentees_True() {
        User mentor = new User();
        long mentorId = 1L;

        mentor.setMentees(new ArrayList<>());
        mentor.setId(mentorId);

        mentor.setMentees(List.of(new User()));
        List<UserDto> userDtoList = List.of(new UserDto(0, "any", "any"));

        Mockito.when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(mentor));
        Mockito.when(userMapper.toDto(mentor.getMentees()))
                .thenReturn(userDtoList);

        List<UserDto> mentees = mentorshipService.getMentees(mentorId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(mentorId);
        assertEquals(1, mentees.size());
    }

    @Test
    void testGetMentorsThrowException() {
        User mentor = new User();
        long mentorId = 1L;

        mentor.setId(mentorId);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(2L));
    }

    @Test
    void getMentors() {
        User mentee = new User();
        long menteeId = 1L;

        mentee.setMentors(new ArrayList<>());
        mentee.setId(menteeId);
        mentee.setMentors(List.of(new User(), new User()));
        List<UserDto> userDtoList = List.of(new UserDto(0, "any", "any"));

        Mockito.when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(mentee));
        Mockito.when(userMapper.toDto(mentee.getMentors()))
                .thenReturn(userDtoList);

        List<UserDto> mentees = mentorshipService.getMentors(menteeId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(menteeId);
        assertEquals(1, mentees.size());
    }

    @Test
    void testGetMenteesThrowException() {
        User mentee = new User();
        long menteeId = 1L;

        mentee.setId(menteeId);

        assertThrows(DataValidationException.class,
                () -> mentorshipService.getMentees(2L));

        mentorId = 1L;
        menteeId = 2L;

        mentor.setId(mentorId);
        mentee.setId(menteeId);

        List<User> mentees = new ArrayList<>();
        mentees.add(mentee);
        List<User> mentors = new ArrayList<>();
        mentors.add(mentor);

        mentor.setMentees(mentees);
        mentee.setMentors(mentors);

        Mockito.when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(mentee));
        Mockito.when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(mentor));
    }

    @Test
    void testDeleteMentee() {
        mentorshipService.deleteMentee(menteeId, mentorId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(menteeId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(mentorId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentor);
    }

    @Test
    void testDeleteMentor() {
        mentorshipService.deleteMentor(menteeId, mentorId);

        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(menteeId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(mentorId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(mentee);
    }

    @Test
    void testThrowExceptions_deleteMentee_DeleteMentor() {
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(menteeId, 3L));
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentee(3L, mentorId));

        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(menteeId, 3L));
        assertThrows(DataValidationException.class,
                () -> mentorshipService.deleteMentor(3L, mentorId));
    }
}