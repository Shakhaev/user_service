package school.faang.user_service.service.goal.operations;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.InvalidInvitationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoalInvitationValidatorTest {

    private final GoalInvitationValidator validator = new GoalInvitationValidator();

    private GoalInvitationDto createValidDto() {
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setInviterId(1L);
        dto.setInvitedUserId(2L);
        return dto;
    }

    private Goal createValidGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        return goal;
    }

    @Test
    void testValidateFailsIfGoalIsNull() {
        GoalInvitationDto dto = createValidDto();

        assertThrows(InvalidInvitationException.class, () -> validator.validate(dto, null),
                "Expected validation to fail if goal is null");
    }

    @Test
    void testValidateFailsIfIdsMatch() {
        Goal goal = createValidGoal();
        GoalInvitationDto dto = createValidDto();
        dto.setInvitedUserId(dto.getInviterId());

        assertThrows(InvalidInvitationException.class, () -> validator.validate(dto, goal),
                "Expected validation to fail if inviterId equals invitedUserId");
    }

    @Test
    void testValidateSuccess() {
        Goal goal = createValidGoal();
        GoalInvitationDto dto = createValidDto();

        assertDoesNotThrow(() -> validator.validate(dto, goal),
                "Expected validation to pass with valid goal and DTO");
    }
}