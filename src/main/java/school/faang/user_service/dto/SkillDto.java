package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SkillDto {
    private long id;

    private String title;
//
//    private List<Long> usersIds;
//
//    private List<Long> guaranteesIds;
//
//    private List<Long> eventIds;
//
//    private List<Long> goalsIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
