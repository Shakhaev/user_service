package school.faang.user_service.validator.goal;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;

import java.util.Arrays;
import java.util.Optional;

@Data
@NoArgsConstructor
@Component
public class GoalInvitationDtoValidator {

    public void validateDto(GoalInvitationDto dto) {
        isNull(dto.getId(), "ID cannot be null");
        isNull(dto.getInviterId(), "InviterID cannot be null");
        isNull(dto.getInvitedUserId(), "InvitedUserID cannot be null");
        isNull(dto.getGoalId(), "GoalID cannot be null");
        isNull(dto.getStatus(), "Goal status cannot be null");

        if (!isValidRequestStatus(dto.getStatus())) {
            throw new IllegalArgumentException(String.format("Invalid RequestStatus: %s", dto.getStatus()));
        }
    }

    private void isNull(Object o, String message) {
        Optional.ofNullable(o)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }

    private boolean isValidRequestStatus(RequestStatus status) {
        return Arrays.stream(RequestStatus.values())
                .anyMatch(validStatus -> validStatus == status);
    }
}