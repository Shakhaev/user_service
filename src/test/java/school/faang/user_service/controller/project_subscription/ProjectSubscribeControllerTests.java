package school.faang.user_service.controller.project_subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.project_subscription.ProjectFollowerService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectSubscribeControllerTests {
    private final static int SKILL_ID = 1;
    private final static int USER_Id = 1;

    @InjectMocks
    private ProjectSubscribeController projectSubscribeController;

    @Mock
    private UserContext userContext;

    @Mock
    private ProjectFollowerService projectFollowerService;

    private long projectId;
    private long followerId;
    private long followeeId;

    @BeforeEach
    void setUp() {
        projectId = 1L;
        followerId = 1L;
        followeeId = 1L;
    }

    @Test
    void testFollowProjectException() {
        doThrow(RuntimeException.class).when(projectFollowerService).followProject(projectId, followerId, followeeId);

        assertThrows(RuntimeException.class, () -> projectSubscribeController.followProject(projectId, followeeId));
    }

    @Test
    void testFollowProject() {
        projectSubscribeController.followProject(projectId, followeeId);

        verify(projectFollowerService, Mockito.times(1))
                .followProject(anyLong(), anyLong(), anyLong());
        verify(userContext, Mockito.times(1)).getUserId();
    }
}
