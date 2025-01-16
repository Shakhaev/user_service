package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.recommendation_dto.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.recommendation_dto.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.skill_offer_dto.CreateSkillOfferRequest;
import school.faang.user_service.dto.recommendation.skill_offer_dto.CreateSkillOfferResponse;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.recommendation.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.*;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public CreateRecommendationResponse create(CreateRecommendationRequest recommendationRequest) {
        Recommendation recommendation = mapCreateRequestToEntity(recommendationRequest);

        // маппить список предлагаемых скиллов в SkillOffer пока не можем, потому что у нас ещё нет recommendationId. Он появится после создания рекомендации
        // CrateSkillOfferRequest хранит в себе только id скиллов, поэтому маппим их в сущности Skill
        List<Skill> offeredSkills = new ArrayList<>();
        recommendationRequest.getSkillOffers().forEach(s -> {
            offeredSkills.add(skillRepository.getReferenceById(s.getSkillId()));
        });

        validateCreateRecommendationRequest(recommendation, offeredSkills);

        Long recommendationId = recommendationRepository.create(recommendation.getAuthor().getId(), recommendation.getReceiver().getId(), recommendation.getContent());
        recommendation.setId(recommendationId);
        saveSkillOffers(recommendation, offeredSkills);

        return mapEntityToCreateResponse(recommendation);
    }

    private Recommendation mapCreateRequestToEntity(CreateRecommendationRequest recommendationRequest) {
        Recommendation recommendation = recommendationMapper.createRequestToEntity(recommendationRequest);

        User author = userRepository.getReferenceById(recommendationRequest.getAuthorId());
        recommendation.setAuthor(author);

        User receiver = userRepository.getReferenceById(recommendationRequest.getReceiverId());
        recommendation.setReceiver(receiver);

        return recommendation;
    }

    private CreateRecommendationResponse mapEntityToCreateResponse(Recommendation recommendation) {
        CreateRecommendationResponse recommendationResponse = recommendationMapper.entityToCreateResponse(recommendation);

        List<CreateSkillOfferResponse> skillOfferResponses = new ArrayList<>();
        recommendation.getSkillOffers().forEach(s -> {
            skillOfferResponses.add(skillOfferMapper.entityToCreateResponse(s));
        });
        recommendationResponse.setSkillOffers(skillOfferResponses);

        return recommendationResponse;
    }

    private void validateCreateRecommendationRequest(Recommendation recommendation, List<Skill> offeredSkills) {
        // проверка, что последняя рекомендация была не раньше, чем 6 месяцев назад
        checkLastRecommendationTime(recommendation);

        // проверка, что навыки, предлагаемые в рекомендации, существуют
        checkSkillsExisting(offeredSkills);
    }


    private void checkLastRecommendationTime(Recommendation recommendation) {
        Optional<Recommendation> recommendationOptional =  recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthor().getId(), recommendation.getReceiver().getId());
        if (recommendationOptional.isPresent()) {
            Recommendation lastRecommendation = recommendationOptional.get();

            Period timeDifference = Period.between(lastRecommendation.getCreatedAt().toLocalDate(), now().toLocalDate());

            if (timeDifference.getMonths() < 6) {
                throw new DataValidationException("The author can make a recommendation to the user no earlier than 6 months after the last recommendationDto. The last recommendationDto was given " + lastRecommendation.getCreatedAt());
            }
        }
    }

    private void checkSkillsExisting(List<Skill> offeredSkills) {
        offeredSkills.forEach(skill -> {
            if (!skillRepository.existsById(skill.getId())) {
                throw new DataValidationException("Skill with ID = " + skill.getId() + " doesn't exist");
            }
        });
    }

    private void saveSkillOffers(Recommendation recommendation, List<Skill> offeredSkills) {
        offeredSkills.forEach(skill -> {
            Long skillOfferId = skillOfferRepository.create(skill.getId(), recommendation.getId());
            //recommendation.addSkillOffer(skillOfferRepository.findById(skillOfferId).orElseThrow());
            addGuarantee(recommendation, skill);
        });
    }

    private void addGuarantee(Recommendation recommendation, Skill skill) {
        UserSkillGuarantee guarantee = userSkillGuaranteeRepository.findByUserIdAndSkillId(recommendation.getReceiver().getId(), skill.getId());
        if (guarantee == null) {
            UserSkillGuarantee newGuarantee = new UserSkillGuarantee(null, recommendation.getReceiver(), skill, recommendation.getAuthor());
            userSkillGuaranteeRepository.save(newGuarantee);
        } else {
            userSkillGuaranteeRepository.updateGuarantor(recommendation.getReceiver().getId(), skill.getId(), recommendation.getAuthor().getId());
        }
    }
}
