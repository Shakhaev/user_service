package school.faang.user_service.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.calculator.leaderboard.SkillsCalculator;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SkillsCalculatorTest {
    private SkillsCalculator skillsCalculator;
    private LeaderboardDto leaderboardDto;
    private static final long ID = 1L;

    @BeforeEach
    public void beforeEach() {
        skillsCalculator = new SkillsCalculator();
        leaderboardDto = new LeaderboardDto();
    }

    @Test
    public void testGetScoreFromNullList() {
        leaderboardDto.setSkillsId(null);

        Integer expected = 0;
        Integer result = skillsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScoreFromEmptyList() {
        leaderboardDto.setSkillsId(Collections.EMPTY_LIST);

        Integer expected = 0;
        Integer result = skillsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScore() {
        leaderboardDto.setSkillsId(List.of(ID));

        Integer expected = 1;
        Integer result = skillsCalculator.getScore(leaderboardDto);

        assertEquals(expected, result);
    }
}
