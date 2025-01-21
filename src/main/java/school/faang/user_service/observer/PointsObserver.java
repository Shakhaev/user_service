package school.faang.user_service.observer;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;

@Component
public class PointsObserver implements SkillObserver {
    @Override
    public void onSkillCreate(Skill event) {
        System.out.println("Начисляем очко");
    }
}
