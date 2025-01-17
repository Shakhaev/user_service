package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.filter.goal.validation.GoalFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalInvitationMapper goalInvitationMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private GoalInvitationDto createTestInvitationDto(Long invitedUserId) {
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setId(1L);
        dto.setInviterId(100L);
        dto.setInvitedUserId(invitedUserId);
        dto.setGoalId(300L);
        dto.setStatus(RequestStatus.PENDING);
        return dto;
    }

    private GoalInvitation createTestInvitation() {
        User inviter = new User();
        inviter.setId(100L);
        inviter.setUsername("Inviter");

        User invited = new User();
        invited.setId(200L);
        invited.setUsername("Invited");

        Goal goal = new Goal();
        goal.setId(300L);

        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(1L);
        invitation.setStatus(RequestStatus.PENDING);
        invitation.setInviter(inviter);
        invitation.setInvited(invited);
        invitation.setGoal(goal);

        return invitation;
    }

    @Test
    void testCreateInvitationFailsForSameUser() {
        GoalInvitationDto testInvitationDto = createTestInvitationDto(100L);
        GoalInvitation mockInvitation = createTestInvitation();

        when(goalInvitationMapper.toEntity(testInvitationDto)).thenReturn(mockInvitation);

        InvalidInvitationException exception = assertThrows(
                InvalidInvitationException.class,
                () -> goalInvitationService.createInvitation(testInvitationDto)
        );

        assertEquals("Inviter and invited user cannot be the same.", exception.getMessage());
        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    void testCreateInvitationSuccess() {
        GoalInvitationDto testInvitationDto = createTestInvitationDto(200L);
        GoalInvitation testInvitation = createTestInvitation();

        when(goalInvitationMapper.toEntity(testInvitationDto)).thenReturn(testInvitation);
        when(goalInvitationRepository.save(testInvitation)).thenReturn(testInvitation);

        assertDoesNotThrow(() -> goalInvitationService.createInvitation(testInvitationDto));
        verify(goalInvitationRepository, times(1)).save(testInvitation);
    }

    @Test
    void testAcceptGoalInvitationSuccess() {
        GoalInvitation testInvitation = createTestInvitation();

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));

        assertDoesNotThrow(() -> goalInvitationService.acceptGoalInvitation(1L));
        assertEquals(RequestStatus.ACCEPTED, testInvitation.getStatus());
        verify(goalInvitationRepository, times(1)).save(testInvitation);
    }

    @Test
    void testRejectGoalInvitationSuccess() {
        GoalInvitation testInvitation = createTestInvitation();

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));

        assertDoesNotThrow(() -> goalInvitationService.rejectGoalInvitation(1L));
        assertEquals(RequestStatus.REJECTED, testInvitation.getStatus());
        verify(goalInvitationRepository, times(1)).save(testInvitation);
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