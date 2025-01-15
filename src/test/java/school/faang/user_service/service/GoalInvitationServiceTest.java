package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.GoalInvitationException;
import school.faang.user_service.filter.goal.InvitationFilter;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<InvitationFilter> invitationFilters;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitation invitation;

    @BeforeEach
    public void setUp() {
//        MockitoAnnotations.openMocks(this);

        invitation = GoalInvitation.builder()
                .id(1L)
                .inviter(User.builder().id(1L).username("InviterUser").build())
                .invited(User.builder().id(2L).username("InvitedUser").build())
                .goal(Goal.builder().id(100L).title("Test Goal").build())
                .status(RequestStatus.PENDING)
                .build();
    }

    @Test
    public void testCreateInvitation_Success() {
//        GoalInvitation invitation = mock(GoalInvitation.class);
        User inviter = mock(User.class);
        User invited = mock(User.class);
        Goal goal = mock(Goal.class);

        when(invitation.getInviter()).thenReturn(inviter);
        when(invitation.getInvited()).thenReturn(invited);
        when(inviter.getId()).thenReturn(1L);
        when(invited.getId()).thenReturn(2L);
        when(goal.getTitle()).thenReturn("Test Goal");
        when(invitation.getStatus()).thenReturn(RequestStatus.PENDING);
        when(invitation.getGoal()).thenReturn(goal);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(goalInvitationRepository.save(invitation)).thenReturn(invitation);

        GoalInvitation result = goalInvitationService.createInvitation(invitation);

        assertEquals(RequestStatus.PENDING, result.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    public void testCreateInvitation_Fail_SameUser() {
        GoalInvitation invitation = mock(GoalInvitation.class);
        User user = mock(User.class);

        when(invitation.getInviter()).thenReturn(user);
        when(invitation.getInvited()).thenReturn(user);
        when(user.getId()).thenReturn(1L);

        GoalInvitationException exception = assertThrows(GoalInvitationException.class,
                () -> goalInvitationService.createInvitation(invitation));

        assertEquals("The user doesn't create invitation by-self", exception.getMessage());
    }

    @Test
    public void testAcceptGoalInvitation_Success() {
        GoalInvitation invitation = mock(GoalInvitation.class);
        User invitedUser = mock(User.class);
        Goal goal = mock(Goal.class);
        List<GoalInvitation> receivedGoals = mock(List.class);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(goalInvitationRepository.save(any())).thenAnswer(i -> i.getArguments()[0] );

        when(invitation.getInvited()).thenReturn(invitedUser);
        when(invitedUser.getReceivedGoalInvitations()).thenReturn(receivedGoals);
        when(receivedGoals.size()).thenReturn(2);
        when(invitedUser.getGoals()).thenReturn(mock(List.class));
        when(invitation.getGoal()).thenReturn(goal);
        when(invitation.getStatus()).thenReturn(RequestStatus.ACCEPTED);

        GoalInvitation result = goalInvitationService.acceptGoalInvitation(1L);

        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    public void testAcceptGoalInvitation_Fail_MaxActiveGoals() {
        GoalInvitation invitation = mock(GoalInvitation.class);
        User invitedUser = mock(User.class);
        List<GoalInvitation> receivedGoals = mock(List.class);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitation.getInvited()).thenReturn(invitedUser);
        when(receivedGoals.size()).thenReturn(3);
        when(invitedUser.getReceivedGoalInvitations()).thenReturn(receivedGoals);

        GoalInvitationException exception = assertThrows(GoalInvitationException.class,
                () -> goalInvitationService.acceptGoalInvitation(1L));

        assertEquals("User can not has more than 3 active goals", exception.getMessage());
    }

    @Test
    public void testRejectGoalInvitation_Success() {
        GoalInvitation invitation = mock(GoalInvitation.class);
        Goal goal = mock(Goal.class);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitation.getGoal()).thenReturn(goal);
        when(goal.getTitle()).thenReturn("Test Goal");

        goalInvitationService.rejectGoalInvitation(1L);

        verify(invitation).setStatus(RequestStatus.REJECTED);
    }

    @Test
    public void testGetInvitations_Fail_NoInvitations() {
        when(goalInvitationRepository.findAll()).thenReturn(List.of());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> goalInvitationService.getInvitations(mock(InvitationFilterDto.class)));

        assertEquals("No one goal invitation created", exception.getMessage());
    }
}
