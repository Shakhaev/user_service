package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.filter.InvitationFilter;
import school.faang.user_service.dto.goal.filter.InviterIdFilter;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getActiveGoal;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getGoalInvitationDto;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getInvitationWithExistingGoal;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getInvitationWithNewGoal;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getInviterIdFilter;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getUser;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getUserWithAlreadyExistingGoal;
import static school.faang.user_service.utils.goal.GoalInvitationPrepareData.getUserWithMaxGoals;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {
    private static final long INVITER_ID = 1L;
    private static final long INVITED_USER_ID = 2L;
    private static final long EXISTING_GOAL_ID = 1L;
    private static final long NEW_GOAL_ID = 3L;
    private static final long NEW_GOAL_INVITATION_ID = 1L;

    @Mock
    private GoalInvitationRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private GoalService goalService;

    @Spy
    private GoalInvitationMapperImpl mapper;

    private final List<InvitationFilter> invitationFilters = new ArrayList<>();

    private GoalInvitationService service;

    @BeforeEach
    void init() {
        invitationFilters.add(new InviterIdFilter());
        service = new GoalInvitationService(repository, mapper, userService, goalService, invitationFilters);
    }

    @Test
    public void shouldCreateInvitationSuccessTest() {
        when(userService.existsById(anyLong())).thenReturn(true);
        when(repository.save(any())).thenReturn(any());

        service.createInvitation(getGoalInvitationDto());

        verify(repository).save(any());
    }

    @Test
    public void testCreateInvitationWithNotExistInviterUser() {
        when(userService.existsById(eq(INVITER_ID))).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.createInvitation(getGoalInvitationDto()));
    }

    @Test
    public void testCreateInvitationWithNotExistsInvitedUser() {
        when(userService.existsById(eq(INVITER_ID))).thenReturn(true);
        when(userService.existsById(eq(INVITED_USER_ID))).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.createInvitation(getGoalInvitationDto()));
    }

    @Test
    public void testCreateInvitationWithIdenticalUsers() {
        long sameUserIdForInviterAndInvited = 1L;
        when(userService.existsById(eq(sameUserIdForInviterAndInvited))).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.createInvitation(getGoalInvitationDto()));
    }

    @Test
    public void testAcceptInvitationSuccess() {
        findInvitationByIdMockWithGoal(NEW_GOAL_ID);
        when(userService.findById(eq(INVITED_USER_ID))).thenReturn(getUser(INVITED_USER_ID));
        when(goalService.existsById(eq(NEW_GOAL_ID))).thenReturn(true);
        when(goalService.findById(eq(NEW_GOAL_ID))).thenReturn(getActiveGoal(NEW_GOAL_ID));
        doNothing().when(goalService).update(any());

        service.acceptGoalInvitation(NEW_GOAL_INVITATION_ID);

        verify(repository, times(2)).findById(anyLong());
    }

    @Test
    public void testAcceptInvitationWithMoreThanMaxGoals() {
        findInvitationByIdMockWithGoal(NEW_GOAL_ID);
        when(userService.findById(eq(INVITED_USER_ID))).thenReturn(getUserWithMaxGoals());

        assertThrows(IllegalArgumentException.class,
                () -> service.acceptGoalInvitation(NEW_GOAL_INVITATION_ID));
    }

    @Test
    public void testAcceptInvitationWithAlreadyExistingGoal() {
        findInvitationByIdMockWithGoal(EXISTING_GOAL_ID);
        when(userService.findById(eq(INVITED_USER_ID))).thenReturn(getUserWithAlreadyExistingGoal());
        when(goalService.existsById(eq(EXISTING_GOAL_ID))).thenReturn(true);
        when(goalService.findById(eq(EXISTING_GOAL_ID))).thenReturn(getActiveGoal(EXISTING_GOAL_ID));

        assertThrows(IllegalArgumentException.class,
                () -> service.acceptGoalInvitation(NEW_GOAL_INVITATION_ID));
    }

    @Test
    public void testAcceptInvitationWithNotExistGoal() {
        findInvitationByIdMockWithGoal(NEW_GOAL_ID);
        when(userService.findById(eq(INVITED_USER_ID))).thenReturn(getUser(INVITED_USER_ID));
        when(goalService.existsById(eq(NEW_GOAL_ID))).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.acceptGoalInvitation(NEW_GOAL_INVITATION_ID));
    }

    @Test
    public void testRejectInvitationSuccessTest() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(getInvitationWithNewGoal(RequestStatus.PENDING)));
        when(goalService.existsById(eq(NEW_GOAL_ID))).thenReturn(true);

        service.rejectGoalInvitation(NEW_GOAL_INVITATION_ID);

        verify(repository).save(getInvitationWithNewGoal(RequestStatus.REJECTED));
    }

    @Test
    public void testRejectInvitationWithNotExistGoalId() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(getInvitationWithNewGoal(RequestStatus.PENDING)));
        when(goalService.existsById(eq(NEW_GOAL_ID))).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.rejectGoalInvitation(NEW_GOAL_INVITATION_ID));
    }

    @Test
    public void testGetInvitationSuccessTestWithFilters() {
        when(repository.findAll()).thenReturn(List.of(getInvitationWithExistingGoal()));

        List<GoalInvitationDto> invitations = service.getInvitationsWithFilters(getInviterIdFilter());

        verify(repository).findAll();
        assertEquals(1, invitations.size());
    }

    private void findInvitationByIdMockWithGoal(long goalId) {
        Optional<GoalInvitation> goalInvitationEntity = Optional.of(
                mapper.toEntity(getGoalInvitationDto(NEW_GOAL_INVITATION_ID, INVITER_ID, INVITED_USER_ID, goalId))
        );
        when(repository.findById(eq(NEW_GOAL_INVITATION_ID)))
                .thenReturn(goalInvitationEntity);
    }
}