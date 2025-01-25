package school.faang.user_service.controller.mentorship;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import school.faang.user_service.controller.MentorshipController;
import school.faang.user_service.exception.EntityNotFoundException;

public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Test
    public void testMentorshipController() {

        //Тестируем проверки на null в методе getMentees
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipController.getMentees(null)
        );

        //Тестируем проверки на null в методе getMentors
        Assert.assertThrows(
                EntityNotFoundException.class,
                () ->
                        mentorshipController.getMentors(null)
        );

        //Тестируем проверки на null в методе deleteMentee
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipController.deleteMentee(null, null)
        );

        //Тестируем проверки на null в методе deleteMentor
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> mentorshipController.deleteMentor(null, null)
        );
    }
}
