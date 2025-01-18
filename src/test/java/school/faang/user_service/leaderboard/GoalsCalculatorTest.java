package school.faang.user_service.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.calculator.leaderboard.GoalsCalculator;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoalsCalculatorTest {
    private GoalsCalculator goalsCalculator;
    private LeaderboardDto leaderboardDto;
    private static final long ID = 1L;

    @BeforeEach
    public void beforeEach() {
        goalsCalculator = new GoalsCalculator();
        leaderboardDto = new LeaderboardDto();
    }

    @Test
    public void testGetScoreFromNullList() {
        leaderboardDto.setGoalsId(null);

        Integer expected = 0;
        Integer result = goalsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScoreFromEmptyList() {
        leaderboardDto.setGoalsId(Collections.EMPTY_LIST);

        Integer expected = 0;
        Integer result = goalsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScore() {
        leaderboardDto.setGoalsId(List.of(ID));

        Integer expected = goalsCalculator.getACTIVITY_SCORE_INDEX();
        Integer result = goalsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }
}
