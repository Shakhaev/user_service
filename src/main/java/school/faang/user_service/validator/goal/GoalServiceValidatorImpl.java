package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exception.data.DataNotMatchException;
import school.faang.user_service.repository.goal.GoalRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalServiceValidatorImpl implements GoalServiceValidator {

    @Value("${limits.active-goals}")
    private int ACTIVE_GOALS_LIMIT;
    private final GoalRepository goalRepository;

    @Override
    @Transactional
    public void validateActiveGoalsLimit(Long userId) {
        if (ACTIVE_GOALS_LIMIT <= goalRepository.countActiveGoalsPerUser(userId)) {
            log.info("user with id:{} already have enough active goals", userId);
            throw new DataNotMatchException("already have enough active goals", userId);
        }
    }
}
