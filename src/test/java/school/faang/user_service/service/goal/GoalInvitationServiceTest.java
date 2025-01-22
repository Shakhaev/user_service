package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.extension.ExtendWith;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.operations.GoalInvitationValidator;
import school.faang.user_service.service.goal.operations.StatusUpdater;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalInvitationValidator goalInvitationValidator;

    @Mock
    private StatusUpdater statusUpdater;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationMapper goalInvitationMapper;

    @BeforeEach
    void setUp() {
        goalInvitationMapper = Mappers.getMapper(GoalInvitationMapper.class);
        goalInvitationService = new GoalInvitationService(
                goalInvitationRepository,
                goalInvitationMapper,
                goalInvitationValidator,
                statusUpdater,
                List.of()
        );
    }

    @Test
    void testCreateInvitation_Success() {
        GoalInvitationDto invitationDto = new GoalInvitationDto();
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(2L);
        invitationDto.setGoalId(3L);

        GoalInvitation invitation = goalInvitationMapper.toEntity(invitationDto);


        doNothing().when(goalInvitationValidator).validate(invitationDto, invitation.getGoal());
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(invitation);

        GoalInvitationDto result = goalInvitationService.createInvitation(invitationDto);

        assertNotNull(result);
        assertEquals(invitationDto.getInviterId(), result.getInviterId());
        verify(goalInvitationValidator).validate(invitationDto, invitation.getGoal());
        verify(goalInvitationRepository).save(any(GoalInvitation.class));
    }

    @Test
    void testAcceptGoalInvitation_Success() {
        GoalInvitation invitation = new GoalInvitation();
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        doNothing().when(goalInvitationValidator).validate(invitation);
        doNothing().when(statusUpdater).updateStatus(invitation, RequestStatus.ACCEPTED);

        GoalInvitationDto result = goalInvitationService.acceptGoalInvitation(1L);

        assertNotNull(result);
        verify(goalInvitationValidator).validate(invitation);
        verify(statusUpdater).updateStatus(invitation, RequestStatus.ACCEPTED);
    }

    @Test
    void testRejectGoalInvitation_Success() {
        GoalInvitation invitation = new GoalInvitation();
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        doNothing().when(goalInvitationValidator).validate(invitation);
        doNothing().when(statusUpdater).updateStatus(invitation, RequestStatus.REJECTED);

        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(1L);

        assertNotNull(result);
        verify(goalInvitationValidator).validate(invitation);
        verify(statusUpdater).updateStatus(invitation, RequestStatus.REJECTED);
    }

    @Test
    void testInvalidInvitationException() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(InvalidInvitationException.class, () -> {
            goalInvitationService.acceptGoalInvitation(1L);
        });

        assertEquals("Invitation does not exist.", exception.getMessage());
    }
}