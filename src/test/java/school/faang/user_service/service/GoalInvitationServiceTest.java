package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private List<InvitationFilter> invitationFilters;

    @Test
    public void testCreateInvitationSuccess() {
        GoalInvitation invitation = GoalInvitation.builder()
                .id(1L)
                .inviter(User.builder().id(1L).username("InviterUser").build())
                .invited(User.builder().id(2L).username("InvitedUser").build())
                .goal(Goal.builder().id(100L).title("Test Goal").build())
                .status(RequestStatus.PENDING)
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(goalInvitationRepository.save(invitation)).thenReturn(invitation);

        GoalInvitation result = goalInvitationService.createInvitation(invitation);

        assertEquals(RequestStatus.PENDING, result.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    public void testCreateInvitationFailSameUser() {
        GoalInvitation invitation = GoalInvitation.builder()
                .inviter(User.builder().id(1L).build())
                .invited(User.builder().id(1L).build())
                .build();

        GoalInvitationException exception = assertThrows(GoalInvitationException.class,
                () -> goalInvitationService.createInvitation(invitation));

        assertEquals("User cannot create invitation for himself", exception.getMessage());
    }

    @Test
    public void testAcceptGoalInvitationSuccess() {
        GoalInvitation invitation = mock(GoalInvitation.class);
        User invitedUser = mock(User.class);
        Goal goal = mock(Goal.class);

        List<GoalInvitation> receivedGoals = mock(List.class);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(goalInvitationRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        when(invitation.getInvited()).thenReturn(invitedUser);
        when(invitedUser.getReceivedGoalInvitations()).thenReturn(receivedGoals);
        when(receivedGoals.size()).thenReturn(2);
        when(invitedUser.getGoals()).thenReturn(new ArrayList<>());
        when(invitation.getGoal()).thenReturn(goal);
        when(invitation.getStatus()).thenReturn(RequestStatus.ACCEPTED);

        GoalInvitation result = goalInvitationService.acceptGoalInvitation(1L);

        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    public void testAcceptGoalInvitationFailMaxActiveGoals() {
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
    public void testRejectGoalInvitationSuccess() {

        GoalInvitation invitation = mock(GoalInvitation.class);
        Goal goal = mock(Goal.class);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitation.getGoal()).thenReturn(goal);
        when(goal.getTitle()).thenReturn("Test Goal");

        goalInvitationService.rejectGoalInvitation(1L);

        verify(invitation).setStatus(RequestStatus.REJECTED);
    }

    @Test
    public void testGetInvitationsFailNoInvitations() {
        when(goalInvitationRepository.findAll()).thenReturn(List.of());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> goalInvitationService.getInvitations(mock(InvitationFilterDto.class)));

        assertEquals("No one goal invitation created", exception.getMessage());
    }

    @Test
    public void testGetInvitationsSuccess() {
        InvitationFilter filter1 = mock(InvitationFilter.class);
        InvitationFilter filter2 = mock(InvitationFilter.class);

        when(filter1.isApplicable(any())).thenReturn(true);
        when(filter1.apply(any(), any())).thenAnswer(invocation -> {
            Stream<GoalInvitation> stream = invocation.getArgument(0);
            return stream.filter(i -> i.getId() == 1L);
        });

        when(filter2.isApplicable(any())).thenReturn(false);

        invitationFilters = List.of(filter1, filter2);
        goalInvitationService = new GoalInvitationService(goalInvitationRepository, userRepository, invitationFilters);

        GoalInvitation invitation1 = GoalInvitation.builder().id(1L).build();
        GoalInvitation invitation2 = GoalInvitation.builder().id(2L).build();
        List<GoalInvitation> invitations = List.of(invitation1, invitation2);

        when(goalInvitationRepository.findAll()).thenReturn(invitations);

        List<GoalInvitation> result = goalInvitationService.getInvitations(new InvitationFilterDto());

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testGetInvitations_ByInviterName() {
        GoalInvitation invitation1 = GoalInvitation.builder()
                .id(1L).inviter(User.builder()
                        .username("John")
                        .build())
                .build();
        GoalInvitation invitation2 = GoalInvitation.builder()
                .id(2L).inviter(User.builder()
                        .username("Alex")
                        .build())
                .build();

        List<GoalInvitation> invitations = List.of(invitation1, invitation2);

        InvitationFilter inviterNameFilter = mock(InvitationFilter.class);
        when(inviterNameFilter.isApplicable(any())).thenReturn(true);
        when(inviterNameFilter.apply(any(), any())).thenAnswer(invocation -> {
            Stream<GoalInvitation> stream = invocation.getArgument(0);
            return stream.filter(i -> "John".equals(i.getInviter().getUsername()));
        });

        invitationFilters = List.of(inviterNameFilter);
        goalInvitationService = new GoalInvitationService(goalInvitationRepository, userRepository, invitationFilters);

        when(goalInvitationRepository.findAll()).thenReturn(invitations);

        List<GoalInvitation> result = goalInvitationService.getInvitations(new InvitationFilterDto());

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getInviter().getUsername());
    }
}