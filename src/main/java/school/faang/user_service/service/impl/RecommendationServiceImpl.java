package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    public static final Integer LAST_RECOMMENDATION_PERIOD = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Override
    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        RecommendationDto createdRecommendation = create(recommendation);
        saveSkillOffers(createdRecommendation);

        return createdRecommendation;
    }

    private void saveSkillOffers(RecommendationDto recommendation) {
        for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {
            createSkillOffer(recommendation, skillOffer);

            if (skillRepository
                    .findUserSkill(skillOffer.getSkillId(), recommendation.getReceiverId())
                    .isPresent()) {
                if (!userSkillGuaranteeRepository
                        .existsUserSkillGuaranteeBySkill_IdAndGuarantor_id(skillOffer.getSkillId(), recommendation.getAuthorId())) {
                    userSkillGuaranteeRepository
                            .create(recommendation.getReceiverId(), skillOffer.getSkillId(), recommendation.getAuthorId());
                }
            }
        }
    }


    public RecommendationDto create(RecommendationDto recommendation) {
        checkForLastRecommendationPeriod(recommendation);
        Long recommendationId = recommendationRepository
                .create(recommendation.getAuthorId(),
                        recommendation.getReceiverId(),
                        recommendation.getContent());
        recommendation.setId(recommendationId);

        return recommendation;
    }

    @Override
    public Recommendation updateRecommendation(RecommendationDto updated) {
        validateRecommendation(updated);
        checkForLastRecommendationPeriod(updated);
        skillOfferRepository.deleteAllByRecommendationId(updated.getId());

        for (SkillOfferDto skillOfferDto: updated.getSkillOffers()){
            createSkillOffer(updated, skillOfferDto);
            Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillOfferDto.getSkillId(), updated.getReceiverId());
            optionalSkill.ifPresent(skill -> skill.addGuarantee(
                    UserSkillGuarantee.builder()
                            .skill(skill)
                            .build()
                    // TODO
            ));
        }

        return null;
    }

    private void createSkillOffer(RecommendationDto recommendation, SkillOfferDto skillOffer) {
        skillOfferRepository.create(skillOffer.getSkillId(), recommendation.getId());
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent()
                .trim()
                .isEmpty()) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }
        if (recommendation.getCreatedAt() == null) {
            throw new DataValidationException("Created date is null");
        }

        if (recommendation.getAuthorId() == null || recommendation.getReceiverId() == null) {
            throw new DataValidationException("Author and receiver must be specified");
        }

        if (recommendation.getAuthorId()
                .equals(recommendation.getReceiverId())) {
            throw new DataValidationException("Users cannot give recommendations to themselves");
        }

        if (recommendation.getSkillOffers() != null && !recommendation.getSkillOffers()
                .isEmpty()) {
            checkForExistingSkills(recommendation);
        }
    }

    private void checkForExistingSkills(RecommendationDto recommendation) {
        if (!recommendation.getSkillOffers()
                .stream()
                .allMatch(skillOfferDto ->
                        skillRepository.existsById(skillOfferDto.getSkillId())
                )) {
            throw new DataValidationException("These skills do not exists in system");
        }
    }

    private void checkForLastRecommendationPeriod(RecommendationDto recommendation) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthorId(),
                        recommendation.getReceiverId())
                .ifPresent(lastRecommendation -> {
                    if (lastRecommendation.getCreatedAt()
                            .plusMonths(LAST_RECOMMENDATION_PERIOD)
                            .isBefore(recommendation.getCreatedAt())) {
                        throw new DataValidationException("Recommendation can only be given after 6 months.");
                    }
                });
    }
}
