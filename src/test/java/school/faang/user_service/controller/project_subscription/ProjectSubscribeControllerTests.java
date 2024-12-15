package school.faang.user_service.controller.project_subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.project_subscription.ProjectFollowerService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
//@WebMvcTest(ProjectSubscribeController.class)
public class ProjectSubscribeControllerTests {
    private MockMvc mockMvc;

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

        mockMvc = MockMvcBuilders.standaloneSetup(projectSubscribeController).build();
    }

    @Test
    void testFollowProjectException() {
        doThrow(RuntimeException.class).when(projectFollowerService).followProject(projectId, followerId);

        assertThrows(RuntimeException.class, () -> projectSubscribeController.followProject(projectId));
    }

    @Test
    void testFollowProject() {
        projectSubscribeController.followProject(projectId);

        verify(projectFollowerService, Mockito.times(1))
                .followProject(anyLong(), anyLong());
        verify(userContext, Mockito.times(1)).getUserId();
    }

    @Test
    void testFollowProjectRequest() throws Exception {
        mockMvc.perform(put("/api/v1/projectId/" + projectId))
                .andExpect(status().isOk());
    }
}