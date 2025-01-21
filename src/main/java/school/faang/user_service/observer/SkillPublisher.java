package school.faang.user_service.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillPublisher {

    private final List<SkillObserver> observers;

    public void notify(Skill skillDto) {
        for (SkillObserver observer : observers) {
            observer.onSkillCreate(skillDto);
        }
    }
}
