package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testDeactivationInvocation() {
        long userId = 1;
        mentorshipService.deactivateMentorship(userId);
        verify(mentorshipRepository, times(1)).deactivateMentorship(userId);
    }
}