package school.faang.user_service.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.calculator.leaderboard.OwnedEventsCalculator;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OwnedEventsCalculatorTest {
    private OwnedEventsCalculator ownedEventsCalculator;
    private LeaderboardDto leaderboardDto;
    private static final long ID = 1L;

    @BeforeEach
    public void beforeEach() {
        ownedEventsCalculator = new OwnedEventsCalculator();
        leaderboardDto = new LeaderboardDto();
    }

    @Test
    public void testGetScoreFromNullList() {
        leaderboardDto.setOwnedEventsId(null);

        Integer expected = 0;
        Integer result = ownedEventsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScoreFromEmptyList() {
        leaderboardDto.setOwnedEventsId(Collections.EMPTY_LIST);

        Integer expected = 0;
        Integer result = ownedEventsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScore() {
        leaderboardDto.setOwnedEventsId(List.of(ID));

        Integer expected = ownedEventsCalculator.getACTIVITY_SCORE_INDEX();
        Integer result = ownedEventsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }
}
