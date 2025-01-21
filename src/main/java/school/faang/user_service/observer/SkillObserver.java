package school.faang.user_service.observer;

import school.faang.user_service.entity.Skill;

public interface SkillObserver {
    void onSkillCreate(Skill event);
}
