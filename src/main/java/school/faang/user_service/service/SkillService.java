package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public List<Skill> getSkills(List<Long> ids) {
        log.info("Getting Skills with ids {}", ids);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Skill> skills = skillRepository.findAllByIds(ids);
        if (skills.size() != ids.size()) {
            List<Long> missingSkillsIds = ids.stream()
                    .filter(id -> skills.stream()
                            .noneMatch(skill -> Objects.equals(skill.getId(), id)))
                    .toList();
            log.warn("Not found skills with ids {}", missingSkillsIds);
        }
        return skills;
    }
}
