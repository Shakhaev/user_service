package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.dto.user.UserMentorshipDto;
import school.faang.user_service.service.MentorshipRelationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {
    @Mock
    private MentorshipRelationService mentorshipRelationService;

    @InjectMocks
    private MentorshipController mentorshipController;

    private List<UserMentorshipDto> mentees;
    private List<UserMentorshipDto> mentors;

    @BeforeEach
    void beforeEach(){
        UserMentorshipDto firstMentee = new UserMentorshipDto();
        firstMentee.setId(2L);
        firstMentee.setUsername("mentee1");
        UserMentorshipDto secondMentee = new UserMentorshipDto();
        secondMentee.setId(3L);
        secondMentee.setUsername("mentee2");

        UserMentorshipDto firstMentor = new UserMentorshipDto();
        firstMentor.setId(1L);
        firstMentor.setUsername("mentor1");
        UserMentorshipDto secondMentor = new UserMentorshipDto();
        secondMentor.setId(4L);
        secondMentor.setUsername("mentor2");

        mentees = List.of(
                firstMentee,
                secondMentee
        );
        mentors = List.of(
                firstMentor,
                secondMentor
        );
    }

    @Test
    void testGetMentees() {
        long mentorId = 1L;
        when(mentorshipRelationService.getMentees(mentorId)).thenReturn(mentees);
        List<UserMentorshipDto> result = mentorshipController.getMentees(mentorId);

        assertEquals(2, result.size());
        assertEquals("mentee1", result.get(0).getUsername());
        assertEquals("mentee2", result.get(1).getUsername());
        verify(mentorshipRelationService).getMentees(mentorId);
    }

    @Test
    void testGetMentors() {
        long userId = 2L;
        when(mentorshipRelationService.getMentors(userId)).thenReturn(mentors);
        List<UserMentorshipDto> result = mentorshipController.getMentors(userId);

        assertEquals(2, result.size());
        assertEquals("mentor1", result.get(0).getUsername());
        assertEquals("mentor2", result.get(1).getUsername());
        verify(mentorshipRelationService).getMentors(userId);
    }

    @Test
    void testDeleteMentee() {
        long mentorId = 1L;
        long menteeId = 2L;

        mentorshipController.deleteMentee(mentorId, menteeId);

        verify(mentorshipRelationService).deleteMentee(menteeId, mentorId);
    }

    @Test
    void testDeleteMentor() {
        long menteeId = 2L;
        long mentorId = 1L;

        mentorshipController.deleteMentor(menteeId, mentorId);

        verify(mentorshipRelationService).deleteMentor(menteeId, mentorId);
    }
}
