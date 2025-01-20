package school.faang.user_service.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.calculator.leaderboard.RecommendationsReceivedCalculator;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecommendationsReceivedCalculatorTest {
    private RecommendationsReceivedCalculator recommendationsReceivedCalculator;
    private LeaderboardDto leaderboardDto;
    private static final long ID = 1L;

    @BeforeEach
    public void beforeEach() {
        recommendationsReceivedCalculator = new RecommendationsReceivedCalculator();
        leaderboardDto = new LeaderboardDto();
    }

    @Test
    public void testGetScoreFromNullList() {
        leaderboardDto.setRecommendationsReceivedId(null);

        Integer expected = 0;
        Integer result = recommendationsReceivedCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScoreFromEmptyList() {
        leaderboardDto.setRecommendationsReceivedId(Collections.EMPTY_LIST);

        Integer expected = 0;
        Integer result = recommendationsReceivedCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScore() {
        leaderboardDto.setRecommendationsReceivedId(List.of(ID));

        Integer expected = recommendationsReceivedCalculator.getACTIVITY_SCORE_INDEX();
        Integer result = recommendationsReceivedCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }
}
