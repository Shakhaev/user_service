package school.faang.user_service.service.project_subscription;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.ProjectServiceClient;
import school.faang.user_service.dto.messaging.ProjectFollowerEvent;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.messaging.FollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectFollowerServiceTests {
    @Mock
    private ProjectSubscriptionRepository projectSubscriptionRepository;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private FollowerEventPublisher publisher;

    @InjectMocks
    private ProjectFollowerService projectFollowerService;

    private final int projectId = 1;

    @Test
    void tesFollowProjectConditionsException() {
        int followerId = 1;
        int projectId = 2;
        doThrow(EntityNotFoundException.class).when(projectServiceClient).getProjectById(anyLong());

        assertThrows(EntityNotFoundException.class,
                () -> projectFollowerService.followProject(projectId, followerId));
    }

    @Test
    void tesFollowProject() {
        int followerId = 1;
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        when(projectServiceClient.getProjectById(anyLong())).thenReturn(projectDto);
        projectFollowerService.followProject(projectId, followerId);

        verify(publisher, Mockito.times(1))
                .publish(any(ProjectFollowerEvent.class));
    }
}
