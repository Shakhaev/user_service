package school.faang.user_service.leaderboard;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.calculator.leaderboard.ExperienceCalculator;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExperienceCalculatorTest {
    private ExperienceCalculator experienceCalculator;
    private LeaderboardDto leaderboardDto;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void beforeEach() {
        experienceCalculator = new ExperienceCalculator();
        leaderboardDto = new LeaderboardDto();
    }

    @Test
    public void testGetScoreFromNullList() {
        leaderboardDto.setExperience(null);

        Integer expected = 0;
        Integer result = experienceCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }
}
