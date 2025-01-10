package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillDto;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillDto findSkillById(Long id) {
        return skillMapper.toDto(skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found, id: " + id)));
    }
}
