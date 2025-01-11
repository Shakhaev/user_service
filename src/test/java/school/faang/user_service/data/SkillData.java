package school.faang.user_service.data;

import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

public enum SkillData {
    SKILL_DEV(1L, "dev", null, null, null, null,
            LocalDateTime.of(2024, 5, 1, 0, 0),
            LocalDateTime.of(2024, 5, 1, 0, 0));

    private final long id;
    private final String title;
    private final List<User> users;
    private final List<UserSkillGuarantee> guarantees;
    private final List<Event> events;
    private final List<Goal> goals;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    SkillData(long id, String title, List<User> users, List<UserSkillGuarantee> guarantees, List<Event> events, List<Goal> goals, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.users = users;
        this.guarantees = guarantees;
        this.events = events;
        this.goals = goals;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Skill toSkill() {
        return Skill.builder()
                .id(id)
                .title(title)
                .users(users)
                .guarantees(guarantees)
                .events(events)
                .goals(goals)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<UserSkillGuarantee> getGuarantees() {
        return guarantees;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
