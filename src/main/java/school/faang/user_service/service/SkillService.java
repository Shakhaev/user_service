package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {
    private final SkillRepository skillRepository;

    public List<Skill> getSkills(List<Long> ids) {
        log.info("Getting Skills with ids {}", ids);
        if (ids == null) {
            throw new IllegalArgumentException("Skill IDs must not be null");
        }
        Set<Skill> skills = skillRepository.findAllByIds(ids);
        log.info("Skills found");
        if (skills.size() != ids.size()) {
            List<Long> missingSkillsIds = ids.stream()
                    .filter(id -> skills.stream()
                            .noneMatch(skill -> Objects.equals(skill.getId(), id)))
                    .toList();
            throw new NoSuchElementException("Not found skills with ids " + missingSkillsIds);
        }
        return new ArrayList<>(skills);
    }
}
