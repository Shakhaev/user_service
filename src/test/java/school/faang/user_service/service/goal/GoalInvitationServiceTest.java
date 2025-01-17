package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.goal.GoalInvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.validator.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @InjectMocks
    private GoalInvitationService goalInvitationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Spy
    private GoalInvitationMapperImpl mapper;
    @Mock
    private GoalInvitationValidator validator;

    private GoalInvitationDto goalInvitationDto;
    private GoalInvitation goalInvitation;
    private User inviter;
    private User invited;
    private Goal goal;
    private List<GoalInvitationFilter> goalInvitationFilters;
    private GoalInvitationFilter filter;
    private GoalInvitationFilterDto filterDto;
    @Captor
    private ArgumentCaptor<Stream<GoalInvitation>> captor;

    @BeforeEach
    public void init() {
        inviter = new User();
        inviter.setId(1L);
        inviter.setGoals(new ArrayList<>());

        invited = new User();
        invited.setId(2L);
        invited.setGoals(new ArrayList<>());

        goal = new Goal();
        goal.setId(1L);

        goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setId(1L);
        goalInvitationDto.setInviterId(inviter.getId());
        goalInvitationDto.setInvitedUserId(invited.getId());
        goalInvitationDto.setGoalId(goal.getId());
        goalInvitationDto.setStatus(RequestStatus.PENDING);

        goalInvitation = mapper.toEntity(goalInvitationDto);

        filter = mock(GoalInvitationFilter.class);
        goalInvitationFilters = List.of(filter);
        filterDto = new GoalInvitationFilterDto();
        filterDto.setStatus(goalInvitationDto.getStatus());

        goalInvitationService = new GoalInvitationService(goalInvitationRepository,
                userRepository,
                validator,
                mapper,
                goalInvitationFilters);
    }

    @Test
    public void createInvitationSuccess() {
        goalInvitationService.createInvitation(goalInvitationDto);
        verify(validator, atLeastOnce()).validate(goalInvitationDto);
        verify(goalInvitationRepository, atLeastOnce()).save(goalInvitation);
    }

    @Test
    public void acceptInvitationSuccess(){
        when(goalInvitationRepository.findById(goalInvitationDto.getId())).thenReturn(Optional.ofNullable(goalInvitation));
        when(validator.uncrowdedInvitedUser(goalInvitation)).thenReturn(invited);
        when(validator.isGoalExist(goalInvitation.getId())).thenReturn(true);

        goalInvitationService.acceptInvitation(goalInvitationDto.getId());

        verify(goalInvitationRepository, atLeastOnce()).save(goalInvitation);
        verify(userRepository, atLeastOnce()).save(invited);

        assertEquals(goalInvitation.getStatus(), RequestStatus.ACCEPTED);
        assertEquals(invited.getGoals(), List.of(goalInvitation.getGoal()));
    }

    @Test
    public void acceptInvitationNotFound(){
        when(goalInvitationRepository.findById(goalInvitationDto.getId())).thenReturn(Optional.empty());

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> goalInvitationService.acceptInvitation(goalInvitationDto.getId()));
        assertEquals(ex.getMessage(), "Invitation not found");
    }

    @Test
    public void acceptInvitationDeletedGoal(){
        when(goalInvitationRepository.findById(goalInvitationDto.getId())).thenReturn(Optional.ofNullable(goalInvitation));
        when(validator.isGoalExist(goal.getId())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> goalInvitationService.acceptInvitation(goalInvitationDto.getId()));
        assertEquals(ex.getMessage(), "Goal was deleted");
    }

    @Test
    public void rejectInvitationSuccess(){
        when(goalInvitationRepository.findById(goalInvitationDto.getId())).thenReturn(Optional.ofNullable(goalInvitation));
        when(validator.isGoalExist(goalInvitation.getId())).thenReturn(true);

        goalInvitationService.rejectGoalInvitation(goalInvitationDto.getId());

        verify(goalInvitationRepository, atLeastOnce()).save(goalInvitation);

        assertEquals(goalInvitation.getStatus(), RequestStatus.REJECTED);
    }

    @Test
    public void rejectInvitationNotFound(){
        when(goalInvitationRepository.findById(goalInvitationDto.getId())).thenReturn(Optional.empty());

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> goalInvitationService.rejectGoalInvitation(goalInvitationDto.getId()));
        assertEquals(ex.getMessage(), "Invitation not found");
    }

    @Test
    public void rejectInvitationDeletedGoal(){
        when(goalInvitationRepository.findById(goalInvitationDto.getId())).thenReturn(Optional.ofNullable(goalInvitation));
        when(validator.isGoalExist(goal.getId())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> goalInvitationService.rejectGoalInvitation(goalInvitationDto.getId()));
        assertEquals(ex.getMessage(), "Goal was deleted");
    }
    @Test
    public void getInvitations(){
        GoalInvitation notApplicableInvitation = new GoalInvitation();
        notApplicableInvitation.setStatus(RequestStatus.ACCEPTED);
        List<GoalInvitation> invitations = List.of(goalInvitation, notApplicableInvitation);
        Stream<GoalInvitation> invitationStream = invitations.stream();

        when(goalInvitationRepository.findAll()).thenReturn(invitations);
        when(filter.apply(captor.capture(), any()))
                .thenReturn(invitationStream
                        .filter(f -> f.getStatus().equals(filterDto.getStatus())));

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filterDto);

        verify(filter, atLeastOnce()).apply(captor.getValue(), filterDto);
        verify(goalInvitationRepository, atLeastOnce()).findAll();
        assertEquals(1, result.size());
    }
}
