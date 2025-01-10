package school.faang.user_service.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserScoreDto {
    private long userId;
    private Integer experience;
    private List<Long> ownedEventsId;
    private List<Long> menteesId;
    private List<Long> goalsId;
    private List<Long> skillsId;
    private List<Long> participatedEventsId;
    private List<Long> recommendationsReceivedId;
}
