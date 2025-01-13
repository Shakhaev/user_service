package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvitationDtoValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private InvitationDtoValidator invitationDtoValidator;

    private GoalInvitationDto validGoalInvitationDto;

    @BeforeEach
    void setUp() {
        validGoalInvitationDto = new GoalInvitationDto(null,1L,2L,1L, null);
    }

    @Test
    void testValidate_SuccessfulValidation() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(goalRepository.existsById(1L)).thenReturn(true);

        invitationDtoValidator.validate(validGoalInvitationDto);
    }

    @Test
    void testValidate_UserInvitesSelf_ThrowsException() {
        validGoalInvitationDto = new GoalInvitationDto(null,1L,1L,1L, null);

        assertThrows(DataValidationException.class, () ->
                invitationDtoValidator.validate(validGoalInvitationDto)
        );
    }

    @Test
    void testValidate_GoalDoesNotExist_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(goalRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () ->
                invitationDtoValidator.validate(validGoalInvitationDto)
        );
    }
}
