package school.faang.user_service.controller.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalInvitationControllerTest {

    @MockBean
    private GoalInvitationService goalInvitationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateInvitation() throws Exception {
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(2L);
        invitationDto.setGoalId(3L);

        mockMvc.perform(post("/goal-invitation/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitationDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Invitation created successfully"));

        verify(goalInvitationService, times(1)).createInvitation(invitationDto);
    }

    @Test
    public void testAcceptInvitation() throws Exception {
        Long invitationId = 1L;

        mockMvc.perform(post("/goal-invitation/accept/{invitationId}", invitationId))
                .andExpect(status().isOk())
                .andExpect(content().string("Invitation accepted successfully"));

        verify(goalInvitationService, times(1)).acceptGoalInvitation(invitationId);
    }

    @Test
    public void testRejectInvitation() throws Exception {
        Long invitationId = 1L;

        mockMvc.perform(post("/goal-invitation/reject/{invitationId}", invitationId))
                .andExpect(status().isOk())
                .andExpect(content().string("Invitation rejected successfully"));

        verify(goalInvitationService, times(1)).rejectGoalInvitation(invitationId);
    }

    @Test
    public void testGetInvitations() throws Exception {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInviterId(1L);
        filterDto.setInvitedId(2L);
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(2L);
        invitationDto.setGoalId(3L);

        List<GoalInvitationDto> invitations = List.of(invitationDto);
        when(goalInvitationService.getInvitations(filterDto)).thenReturn(invitations);

        mockMvc.perform(get("/goal-invitation/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].inviterId").value(1L))
                .andExpect(jsonPath("$[0].invitedUserId").value(2L))
                .andExpect(jsonPath("$[0].goalId").value(3L));

        verify(goalInvitationService, times(1)).getInvitations(filterDto);
    }
}