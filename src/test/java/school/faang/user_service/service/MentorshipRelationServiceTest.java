package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserMentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMentorshipMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRelationServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserMentorshipMapperImpl userMapper;

    @InjectMocks
    private MentorshipRelationService mentorshipRelationService;

    private User mentor;
    private User mentee;

    @BeforeEach
    void beforeEach() {

        mentor = new User();
        mentor.setId(1L);
        mentor.setUsername("mentorUser");

        mentee = new User();
        mentee.setId(2L);
        mentee.setUsername("menteeUser");

        mentor.setMentees(new ArrayList<>(List.of(mentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor)));
    }

    @Test
    void testGetMenteesUserNotFound() {
        long userId = arrange();

        NoSuchElementException exception = Assert.assertThrows(NoSuchElementException.class, () ->
                mentorshipRelationService.getMentees(userId)
        );

        assertUserNotFound(exception, userId);
    }

    @Test
    void testGetMenteesUserExists() {
        long userId = 1L;
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(mentor));
        List<UserMentorshipDto> result = mentorshipRelationService.getMentees(userId);

        assertUserExist(result, userId);
    }

    @Test
    void testGetMentorsUserNotFound() {
        long userId = arrange();

        NoSuchElementException exception = Assert.assertThrows(NoSuchElementException.class, () ->
                mentorshipRelationService.getMentors(userId)
        );

        assertUserNotFound(exception, userId);
    }

    private long arrange() {
        long userId = 3L;
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.empty());
        return userId;
    }

    private void assertUserNotFound(NoSuchElementException exception, long userId) {
        assertEquals("Не существует пользователя с ID: " + userId, exception.getMessage());
        verify(mentorshipRepository, times(1)).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void testGetMentorsUserExists(){
        long userId = 1L;
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(mentee));
        List<UserMentorshipDto> result = mentorshipRelationService.getMentors(userId);

        assertUserExist(result, userId);
    }

    private void assertUserExist(List<UserMentorshipDto> result, long userId) {
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mentorshipRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteMenteeMentorNotFound() {
        extractingAnEmptyUserMock(mentor);
        NoSuchElementException exception = Assert.assertThrows(NoSuchElementException.class, () -> {
            mentorshipRelationService.deleteMentee(mentee.getId(), mentor.getId());
        });

        assertEquals("Ментор с ID: 1 не найден!!!", exception.getMessage());
    }

    @Test
    void testDeleteMenteeMenteeNotFound() {
        extractingUserMock(mentor);
        mentor.setMentees(new ArrayList<>());

        NoSuchElementException exception = Assert.assertThrows(NoSuchElementException.class, () -> {
            mentorshipRelationService.deleteMentee(mentee.getId(), mentor.getId());
        });
        assertEquals("Менти с ID: 2 не найден!!!", exception.getMessage());
    }

    @Test
    void testDeleteMenteeSuccess() {
        extractingUserMock(mentor);

        mentorshipRelationService.deleteMentee(mentee.getId(), mentor.getId());

        assertTrue(mentor.getMentees().isEmpty());
        verify(mentorshipRepository, times(1)).save(mentor);
    }

    @Test
    void testDeleteMentorMenteeNotFound() {
        extractingAnEmptyUserMock(mentee);
        NoSuchElementException exception = Assert.assertThrows(NoSuchElementException.class, () -> {
            mentorshipRelationService.deleteMentor(mentee.getId(), mentor.getId());
        });

        assertEquals("Менти с ID: 2 не найден!!!", exception.getMessage());
    }

    @Test
    void testDeleteMentorMentorNotFound() {
        extractingUserMock(mentee);
        mentee.setMentors(new ArrayList<>());

        NoSuchElementException exception = Assert.assertThrows(NoSuchElementException.class, () -> {
            mentorshipRelationService.deleteMentor(mentee.getId(), mentor.getId());
        });
        assertEquals("Ментор с ID: 1 не найден!!!", exception.getMessage());
    }

    @Test
    void testDeleteMentorSuccess() {
        extractingUserMock(mentee);

        mentorshipRelationService.deleteMentor(mentee.getId(), mentor.getId());

        assertTrue(mentee.getMentors().isEmpty());
        verify(mentorshipRepository, times(1)).save(mentee);
    }

    private void extractingUserMock(User user) {
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }

    private void extractingAnEmptyUserMock(User user){
        when(mentorshipRepository.findById(user.getId())).thenReturn(Optional.empty());
    }
}