package school.faang.user_service.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.calculator.leaderboard.MenteesCalculator;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenteesCalculatorTest {
    private MenteesCalculator menteesCalculator;
    private LeaderboardDto leaderboardDto;
    private static final long ID = 1L;

    @BeforeEach
    public void beforeEach() {
        menteesCalculator = new MenteesCalculator();
        leaderboardDto = new LeaderboardDto();
    }

    @Test
    public void testGetScoreFromNullList() {
        leaderboardDto.setMenteesId(null);

        Integer expected = 0;
        Integer result = menteesCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScoreFromEmptyList() {
        leaderboardDto.setMenteesId(Collections.EMPTY_LIST);

        Integer expected = 0;
        Integer result = menteesCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScore() {
        leaderboardDto.setMenteesId(List.of(ID));

        Integer expected = menteesCalculator.getACTIVITY_SCORE_INDEX();
        Integer result = menteesCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }
}
