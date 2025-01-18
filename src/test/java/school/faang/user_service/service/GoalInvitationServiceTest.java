package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filters.goal.InvitationFilter;
import school.faang.user_service.filters.goal.InvitationInvitedNameFilter;
import school.faang.user_service.filters.goal.InvitationInviterNameFilter;
import school.faang.user_service.filters.goal.InvitationInviterUserFilter;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalService goalService;

    @Mock
    private UserService userService;

    @Mock
    private List<InvitationFilter> invitationFilters;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateInvitation_Success() {
        Goal goal = Goal.builder()
                .id(1L)
                .title("Learn Java")
                .description("Complete a Java course within 3 months")
                .status(GoalStatus.ACTIVE)
                .build();

        User inviter = User.builder()
                .id(1L)
                .username("JohnDoe")
                .email("john.doe@example.com")
                .active(true)
                .build();

        User invited = User.builder()
                .id(2L)
                .username("JaneSmith")
                .email("jane.smith@example.com")
                .active(true)
                .build();

        GoalInvitation goalInvitation = GoalInvitation.builder()
                .goal(goal)
                .inviter(inviter)
                .invited(invited)
                .status(RequestStatus.PENDING)
                .build();

        when(goalService.getGoalById(1L)).thenReturn(goal);
        when(userService.getUserById(1L)).thenReturn(inviter);
        when(userService.getUserById(2L)).thenReturn(invited);
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);

        GoalInvitation result = goalInvitationService.createInvitation(goalInvitation);

        assertNotNull(result);
        assertEquals(RequestStatus.PENDING, result.getStatus());
        assertEquals(inviter, result.getInviter());
        assertEquals(invited, result.getInvited());
        verify(goalInvitationRepository).save(goalInvitation);
    }

    @Test
    void testCreateInvitation_ThrowsExceptionWhenUsersAreSame() {
        Goal goal = Goal.builder().id(1L).build();
        User user = User.builder().id(1L).build();

        GoalInvitation goalInvitation = GoalInvitation.builder()
                .goal(goal)
                .inviter(user)
                .invited(user)
                .build();

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.createInvitation(goalInvitation));
    }

    @Test
    void testCreateInvitation_ThrowsExceptionWhenUserIsNull() {
        Goal goal = Goal.builder().id(1L).build();

        User inviter = User.builder().id(null).build();
        User invited = User.builder().id(2L).build();

        GoalInvitation goalInvitationWithNullInviter = GoalInvitation.builder()
                .goal(goal)
                .inviter(inviter)
                .invited(invited)
                .build();

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.createInvitation(goalInvitationWithNullInviter));
    }

    @Test
    void testAcceptGoalInvitation_ThrowsExceptionWhenNoInvitationWithId() {
        long nonExistentId = 1L;

        when(goalInvitationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.acceptGoalInvitation(nonExistentId));

    }

    @Test
    void testAcceptGoalInvitation_ThrowsExceptionWhenUserExceedsGoalsLimit() {
        long invitationId = 1L;

        User user1 = User.builder()
                .id(1L)
                .username("JohnDoe")
                .email("john.doe@example.com")
                .build();

        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Learn Java")
                .description("Complete Java basics and OOP concepts")
                .users(List.of(user1))
                .build();

        Goal goal2 = Goal.builder()
                .id(2L)
                .title("Contribute to Open Source")
                .description("Submit PRs to three open-source projects")
                .users(List.of(user1))
                .build();

        Goal goal3 = Goal.builder()
                .id(3L)
                .title("Master Python")
                .description("Complete a Python course and build a project")
                .users(List.of(user1))
                .build();


        User user = User.builder()
                .id(2L)
                .goals(Arrays.asList(goal1, goal2, goal3))
                .build();

        GoalInvitation goalInvitation = GoalInvitation.builder()
                .id(invitationId)
                .invited(user)
                .goal(goal2)
                .build();

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.acceptGoalInvitation(invitationId));

    }


    @Test
    void testAcceptGoalInvitation_ThrowsExceptionWhenUserAlreadyContainsGoal() {
        long invitationId = 1L;

        User user = User.builder()
                .id(2L)
                .goals(Collections.singletonList(Goal.builder().id(1L).build()))
                .build();

        Goal goal = Goal.builder()
                .id(1L)
                .users(Collections.singletonList(user))
                .build();

        GoalInvitation goalInvitation = GoalInvitation.builder()
                .id(invitationId)
                .invited(user)
                .goal(goal)
                .build();

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.acceptGoalInvitation(invitationId));
    }

    @Test
    void testRejectGoalInvitation_ThrowsExceptionWhenNoInvitationWithId() {
        long nonExistentId = 1L;

        when(goalInvitationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.rejectGoalInvitation(nonExistentId));
    }



    @Test
    public void testGetInvitations_Success() {
        Goal goal = Goal.builder()
                .id(1L)
                .title("Contribute to Open Source")
                .description("Find and contribute to three open-source projects")
                .status(GoalStatus.ACTIVE)
                .build();

        User inviter = User.builder()
                .id(1L)
                .username("Alice")
                .email("alice@example.com")
                .build();

        User invited = User.builder()
                .id(2L)
                .username("Bob")
                .email("bob@example.com")
                .build();

        GoalInvitation invitation1 = GoalInvitation.builder()
                .id(1L)
                .goal(goal)
                .inviter(inviter)
                .invited(invited)
                .status(RequestStatus.PENDING)
                .build();

        GoalInvitation invitation2 = GoalInvitation.builder()
                .id(2L)
                .goal(goal)
                .inviter(inviter)
                .invited(User.builder().id(3L).username("Charlie").email("charlie@example.com").build())
                .status(RequestStatus.PENDING)
                .build();

        InvitationFilter filterMock = mock(InvitationFilter.class);
        when(filterMock.isApplicable(any())).thenReturn(true);
        when(filterMock.apply(any(), any())).thenReturn(Stream.of(invitation1, invitation2));

        when(invitationFilters.stream()).thenReturn(Stream.of(filterMock));
        when(goalInvitationRepository.findAll()).thenReturn(Arrays.asList(invitation1, invitation2));

        InvitationFilterDto filterDto = new InvitationFilterDto();
        List<GoalInvitation> result = goalInvitationService.getInvitations(filterDto);

        assertEquals(2, result.size());
    }

    @Test
    void testGetInvitations_withFilters() {
        User user1 = User.builder().id(1L).username("JohnDoe").build();
        User user2 = User.builder().id(2L).username("JaneSmith").build();
        User user3 = User.builder().id(3L).username("MichaelJohnson").build();
        User user4 = User.builder().id(4L).username("EmilyDavis").build();

        GoalInvitation invitation1 = GoalInvitation.builder()
                        .id(1L)
                        .inviter(user1)
                        .invited(user2)
                        .status(RequestStatus.PENDING)
                        .build();

        GoalInvitation invitation2 = GoalInvitation.builder()
                        .id(2L)
                        .inviter(user1)
                        .invited(user3)
                        .status(RequestStatus.PENDING)
                        .build();

        GoalInvitation invitation3 = GoalInvitation.builder()
                .id(3L)
                .inviter(user4)
                .invited(user2)
                .status(RequestStatus.ACCEPTED)
                .build();

        when(goalInvitationRepository.findAll()).thenReturn(Arrays.asList(invitation1, invitation2, invitation3));

        List<InvitationFilter> mockFilters = Arrays.asList(
                new InvitationInvitedNameFilter(),
                new InvitationInviterNameFilter(),
                new InvitationInviterUserFilter(),
                new InvitationInviterNameFilter()
        );

        when(invitationFilters.stream()).thenReturn(mockFilters.stream());

        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInviterId(1L);
        filterDto.setInvitedId(2L);
        filterDto.setInviterNamePattern("John");
        filterDto.setInvitedNamePattern("Smith");

        List<GoalInvitation> result = goalInvitationService.getInvitations(filterDto);

        assertEquals(1, result.size());
        assertEquals(invitation1, result.get(0));
    }
}
