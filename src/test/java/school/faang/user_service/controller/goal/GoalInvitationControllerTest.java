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

        GoalInvitationDto createdInvitation = new GoalInvitationDto();
        createdInvitation.setId(1L);
        createdInvitation.setInviterId(1L);
        createdInvitation.setInvitedUserId(2L);
        createdInvitation.setGoalId(3L);

        when(goalInvitationService.createInvitation(invitationDto)).thenReturn(createdInvitation);

        mockMvc.perform(post("/goal-invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitationDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.inviterId").value(1L))
                .andExpect(jsonPath("$.invitedUserId").value(2L))
                .andExpect(jsonPath("$.goalId").value(3L));

        verify(goalInvitationService, times(1)).createInvitation(invitationDto);
    }

    @Test
    public void testAcceptGoalInvitation() throws Exception {
        Long invitationId = 1L;

        GoalInvitationDto acceptedInvitation = new GoalInvitationDto();
        acceptedInvitation.setId(invitationId);
        acceptedInvitation.setInviterId(1L);
        acceptedInvitation.setInvitedUserId(2L);
        acceptedInvitation.setGoalId(3L);

        when(goalInvitationService.acceptGoalInvitation(invitationId)).thenReturn(acceptedInvitation);

        mockMvc.perform(put("/goal-invitations/accept/{invitationId}", invitationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.inviterId").value(1L))
                .andExpect(jsonPath("$.invitedUserId").value(2L))
                .andExpect(jsonPath("$.goalId").value(3L));

        verify(goalInvitationService, times(1)).acceptGoalInvitation(invitationId);
    }

    @Test
    public void testRejectGoalInvitation() throws Exception {
        Long invitationId = 1L;

        GoalInvitationDto rejectedInvitation = new GoalInvitationDto();
        rejectedInvitation.setId(invitationId);
        rejectedInvitation.setInviterId(1L);
        rejectedInvitation.setInvitedUserId(2L);
        rejectedInvitation.setGoalId(3L);

        when(goalInvitationService.rejectGoalInvitation(invitationId)).thenReturn(rejectedInvitation);

        mockMvc.perform(put("/goal-invitations/reject/{invitationId}", invitationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.inviterId").value(1L))
                .andExpect(jsonPath("$.invitedUserId").value(2L))
                .andExpect(jsonPath("$.goalId").value(3L));

        verify(goalInvitationService, times(1)).rejectGoalInvitation(invitationId);
    }

    @Test
    public void testGetInvitations() throws Exception {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInviterId(1L);
        filterDto.setInvitedId(2L);

        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setId(1L);
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(2L);
        invitationDto.setGoalId(3L);

        List<GoalInvitationDto> invitations = List.of(invitationDto);

        when(goalInvitationService.getInvitations(filterDto)).thenReturn(invitations);

        mockMvc.perform(get("/goal-invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].inviterId").value(1L))
                .andExpect(jsonPath("$[0].invitedUserId").value(2L))
                .andExpect(jsonPath("$[0].goalId").value(3L));

        verify(goalInvitationService, times(1)).getInvitations(filterDto);
    }
}