package school.faang.user_service.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.calculator.leaderboard.ParticipatedEventsCalculator;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParticipatedEventsCalculatorTest {
    private ParticipatedEventsCalculator participatedEventsCalculator;
    private LeaderboardDto leaderboardDto;
    private static final long ID = 1L;

    @BeforeEach
    public void beforeEach() {
        participatedEventsCalculator = new ParticipatedEventsCalculator();
        leaderboardDto = new LeaderboardDto();
    }

    @Test
    public void testGetScoreFromNullList() {
        leaderboardDto.setParticipatedEventsId(null);

        Integer expected = 0;
        Integer result = participatedEventsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScoreFromEmptyList() {
        leaderboardDto.setParticipatedEventsId(Collections.EMPTY_LIST);

        Integer expected = 0;
        Integer result = participatedEventsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScore() {
        leaderboardDto.setParticipatedEventsId(List.of(ID));

        Integer expected = 1;
        Integer result = participatedEventsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }
}
