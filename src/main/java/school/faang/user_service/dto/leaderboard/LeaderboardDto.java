package school.faang.user_service.dto.leaderboard;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LeaderboardDto {
    private long userId;
    private int activityScore;
    private Integer experience;
    private List<Long> ownedEventsId;
    private List<Long> menteesId;
    private List<Long> goalsId;
    private List<Long> skillsId;
    private List<Long> participatedEventsId;
    private List<Long> recommendationsReceivedId;
}
