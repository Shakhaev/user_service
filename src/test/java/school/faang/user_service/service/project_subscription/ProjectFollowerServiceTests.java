package school.faang.user_service.service.project_subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.messaging.ProjectFollowerEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.messaging.FollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectFollowerServiceTests {
    @Mock
    private ProjectSubscriptionRepository projectSubscriptionRepository;

    @Mock
    private FollowerEventPublisher publisher;

    @InjectMocks
    private ProjectFollowerService projectFollowerService;

    private final int projectId = 1;

    @Test
    void tesFollowProjectConditionsException() {
        int followerId = 1;
        int followeeId = 1;
        assertThrows(DataValidationException.class,
                () -> projectFollowerService.followProject(new ProjectFollowerEvent(projectId, followerId, followeeId)));
    }

    @Test
    void tesFollowProject() {
        int followerId = 1;
        int followeeId = 2;

        projectFollowerService.followProject(new ProjectFollowerEvent(projectId, followerId, followeeId));

        verify(publisher, Mockito.times(1))
                .publish(any(ProjectFollowerEvent.class));

    }
}
