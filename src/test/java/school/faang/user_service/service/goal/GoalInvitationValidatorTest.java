package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.validator.GoalInvitationValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {
    @InjectMocks
    private GoalInvitationValidator goalInvitationValidator;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private GoalInvitationMapperImpl mapper;
    private User invited;
    private User inviter;
    private GoalInvitationDto dto;
    private GoalInvitation goalInvitation;

    @BeforeEach
    public void init() {
        dto = new GoalInvitationDto();
        dto.setId(1L);
        dto.setInviterId(1L);
        dto.setInvitedUserId(2L);
        dto.setGoalId(1L);
        dto.setStatus(RequestStatus.PENDING);

        invited = User.builder()
                .id(dto.getInvitedUserId())
                .build();
        inviter = User.builder()
                .id(dto.getInviterId())
                .build();

        goalInvitation = mapper.toEntity(dto);
        goalInvitation.setInvited(invited);
    }

    @Test
    public void validateSuccess() {
        when(userRepository.findById(dto.getInviterId())).thenReturn(Optional.ofNullable(inviter));
        when(userRepository.findById(dto.getInvitedUserId())).thenReturn(Optional.ofNullable(invited));
        when(goalRepository.existsById(dto.getGoalId())).thenReturn(true);

        goalInvitationValidator.validate(dto);

        verify(userRepository, atLeastOnce()).findById(dto.getInviterId());
        verify(userRepository, atLeastOnce()).findById(dto.getInvitedUserId());
        verify(goalRepository, atLeastOnce()).existsById(dto.getGoalId());
    }

    @Test
    public void validateUserNotExist(){
        when(userRepository.findById(dto.getInviterId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validate(dto));
        assertEquals(exception.getMessage(), "User with ID: " + dto.getInviterId() + " does not exist");
    }

    @Test
    public void validateUsersAreSame(){
        inviter.setId(invited.getId());
        when(userRepository.findById(dto.getInviterId())).thenReturn(Optional.ofNullable(inviter));
        when(userRepository.findById(dto.getInvitedUserId())).thenReturn(Optional.ofNullable(invited));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validate(dto));
        assertEquals(exception.getMessage(), "Inviter and Invited are the same person");
    }

    @Test
    public void validateGoalNotExist(){
        when(userRepository.findById(dto.getInviterId())).thenReturn(Optional.ofNullable(inviter));
        when(userRepository.findById(dto.getInvitedUserId())).thenReturn(Optional.ofNullable(invited));
        when(goalRepository.existsById(dto.getGoalId())).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validate(dto));
        assertEquals(exception.getMessage(), "Goal with ID: " + dto.getGoalId() + " does not exist");
    }

    @Test
    public void userExist(){
        when(userRepository.findById(dto.getInviterId())).thenReturn(Optional.ofNullable(inviter));

        goalInvitationValidator.userExist(dto.getInviterId());

        verify(userRepository, atLeastOnce()).findById(dto.getInviterId());
    }

    @Test
    public void isGoalExist(){
        goalInvitationValidator.isGoalExist(dto.getGoalId());

        verify(goalRepository, atLeastOnce()).existsById(dto.getGoalId());
    }

    @Test
    public void uncrowdedInvitedUserIsFull(){
        invited.setGoals(List.of(new Goal(), new Goal(), new Goal()));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> goalInvitationValidator.uncrowdedInvitedUser(goalInvitation));
        assertEquals(exception.getMessage(), "This user is full of work");
    }

    @Test
    public void uncrowdedInvitedUserAlreadyHasGoal(){
        Goal goal = new Goal();
        invited.setGoals(List.of(goal));
        goalInvitation.setGoal(goal);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> goalInvitationValidator.uncrowdedInvitedUser(goalInvitation));
        assertEquals(exception.getMessage(), "This user is already working on this goal");
    }
}
