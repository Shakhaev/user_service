package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper){
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    public SkillDto create(SkillDto skillDto){
        if (skillRepository.existsByTitle(skillDto.getTitle())){
            throw new IllegalArgumentException("Skill already exists.");
        }
        Skill skill = skillMapper.toEntity(skillDto);

        Skill savedSkill = skillRepository.save(skill);

        return skillMapper.toDTO(savedSkill);
    }
    public List<SkillDto> getUserSkills(long userId){
        List<Skill> userSkills = skillRepository.findAllByUserId(userId);
        return userSkills.stream()
                .map(skillMapper::toDTO)
                .collect(Collectors.toList());


    }

}
