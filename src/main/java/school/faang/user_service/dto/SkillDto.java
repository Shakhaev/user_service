package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class SkillDto {
    private long id;
    private String title;
    private List<Long> userIds;
    private List<Long> eventIds;
}
