package school.faang.user_service.controller.mentorship;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.service.MentorshipServiceImpl;

public class MentorshipControllerTest {

    @Mock
    private MentorshipServiceImpl mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;

    @Test
    public void testGetMenteesByIdFailed() {

        //Тестируем проверки на null в методе getMentees
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipController.getMentees(null)
        );
    }

    @Test
    public void testGetMentorsByIdFailed() {

        //Тестируем проверки на null в методе getMentors
        Assert.assertThrows(
                EntityNotFoundException.class,
                () ->
                        mentorshipController.getMentors(null)
        );
    }

    @Test
    public void testDeleteMenteeFailed() {
        //Тестируем проверки на null в методе deleteMentee
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipController.deleteMentee(null, null)
        );
    }

    @Test
    public void testDeleteMentorFailed() {
        //Тестируем проверки на null в методе deleteMentor
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipController.deleteMentor(null, null)
        );
    }

    /*
    @Test
    public void testGetMenteesByIdSuccess() {

    }

    @Test
    public void testGetMentorsByIdSuccess() {
        Assert.assertThrows(
                EntityNotFoundException.class,
                () ->
                        mentorshipController.getMentors(null)
        );
    }

    @Test
    public void testDeleteMenteeSuccess() {
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipController.deleteMentee(null, null)
        );
    }

    @Test
    public void testDeleteMentorSuccess() {
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipController.deleteMentor(null, null)
        );
    }*/
}
