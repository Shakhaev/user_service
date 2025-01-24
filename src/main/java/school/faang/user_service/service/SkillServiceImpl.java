package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.ResponseSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl {
    @Value("${config.value.MIN_SKILL_OFFERS}")
    private int MIN_SKILL_OFFERS;

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private UserRepository userRepository;



    public ResponseSkillDto create(CreateSkillDto skill) throws DataValidationException {
        if (skillRepository.existsByTitle(skill.title())) {
            throw new DataValidationException("The skill = " + skill.title() + " already exists!");
        }

        Skill skillEntity = skillMapper.toSkillEntity(skill);
        skillEntity = skillRepository.save(skillEntity);

        return skillMapper.toSkillDto(skillEntity);
    }

    public List<ResponseSkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        HashMap<Skill, Long> skillsOffers = skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(x -> x, HashMap::new, Collectors.counting()));

        return skillsOffers.entrySet().stream()
                .map(skillLongEntry -> new SkillCandidateDto(skillMapper.toSkillDto(skillLongEntry.getKey()),
                        skillLongEntry.getValue()))
                .toList();
    }

    public ResponseSkillDto acquireSkillFromOffers(long skillId, long userId) {

        Optional<Skill> optSkill = skillRepository.findById(skillId);
        optSkill.orElseThrow(() -> new DataValidationException("Skill with id = " + skillId + " not found"));
        Skill skill = optSkill.get();

        if (skill.getUsers().stream().anyMatch(user -> user.getId().equals(userId))) {
            return null;
        }

        User userToAddGuarantee = userRepository.findById(userId).orElse(null);
        List<SkillOffer>  skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        List<UserSkillGuarantee> userSkillGuarantees = skill.getGuarantees();

        if (skillOffers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);

            skillOffers.forEach(skillOffer -> {
                User guarantorUser = userRepository.findById(skillOffer.getId()).orElse(null);
                UserSkillGuarantee guarantee = new UserSkillGuarantee();
                guarantee.setUser(userToAddGuarantee);
                guarantee.setSkill(skill);
                guarantee.setGuarantor(guarantorUser);
                userSkillGuarantees.add(guarantee);
            });

            skill.setGuarantees(userSkillGuarantees);
            skillRepository.save(skill);
        }
        return skillMapper.toSkillDto(skill);
    }
}
