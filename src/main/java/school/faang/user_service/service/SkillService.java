package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository repository;

    public Skill findSkillById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill with id = " + id + " doesn't exists"));
    }
}