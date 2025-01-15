package school.faang.user_service.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.recommendation_dto.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.recommendation_dto.CreateRecommendationResponse;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.recommendation.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.Period;
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

        // проверка, что последняя рекомендация была не раньше, чем 6 месяцев назад
        checkLastRecommendationTime(recommendation);

        // проверка, что навыки, предлагаемые в рекомендации, существуют
        checkSkillsExisting(recommendation);

        Long id = recommendationRepository.create(recommendation.getAuthor().getId(), recommendation.getReceiver().getId(), recommendation.getContent());
        saveSkillOffers(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    private Recommendation mapCreateRequestToEntity(CreateRecommendationRequest request) {
        Recommendation recommendation = recommendationMapper.createRequestToEntity(request);

        User author = userRepository.getReferenceById(request.getAuthorId());
        recommendation.setAuthor(author);

        User receiver = userRepository.getReferenceById(request.getReceiverId());
        recommendation.setReceiver(receiver);

        List<SkillOffer> skillOffers = request.getSkillOffers().stream().map(skillOfferMapper::toEntity).toList();
        recommendation.setSkillOffers(skillOffers);

        return recommendation;
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

    private void checkSkillsExisting(Recommendation recommendation) {
        recommendation.getSkillOffers().forEach(s -> {
            if (!skillRepository.existsByTitle(s.getSkill().getTitle())) {
                throw new DataValidationException("Skill " + s.getSkill().getTitle() + " doesn't exist");
            }
        });
    }


    private void saveSkillOffers(Recommendation recommendation) {
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            skillOfferRepository.create(skillOffer.getSkill().getId(), skillOffer.getRecommendation().getId());

            // если у пользователя уже есть такой скилл, то добавить автора рекомендации гарантом к скиллу, если он ещё не стоит там
            UserSkillGuarantee guarantee = userSkillGuaranteeRepository.findByUserIdAndSkillId(recommendation.getReceiver().getId(), skillOffer.getSkill().getId());
            if (guarantee == null) {
                UserSkillGuarantee newGuarantee = new UserSkillGuarantee(null, recommendation.getReceiver(), skillOffer.getSkill(), recommendation.getAuthor());
                userSkillGuaranteeRepository.save(newGuarantee);
            } else {
                userSkillGuaranteeRepository.updateGuarantor(recommendation.getReceiver().getId(), skillOffer.getSkill().getId(), recommendation.getAuthor().getId());
            }
        }
    }
}
