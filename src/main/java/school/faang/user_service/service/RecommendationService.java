package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
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
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;

    public Optional<RecommendationDto> create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        // проверка, что последняя рекомендация была не раньше, чем 6 месяцев назад
        Optional<Recommendation> recommendationOptional =  recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthor().getId(), recommendation.getReceiver().getId());
        if (recommendationOptional.isPresent()) {
            Recommendation lastRecommendation = recommendationOptional.get();

            Period timeDifference = Period.between(lastRecommendation.getCreatedAt().toLocalDate(), now().toLocalDate());

            if (timeDifference.getMonths() < 6) {
                throw new DataValidationException("The author can make a recommendation to the user no earlier than 6 months after the last recommendationDto. The last recommendationDto was given " + lastRecommendation.getCreatedAt());
                return Optional.empty();
            }
        }

        // проверка, что навыки, предлагаемые в рекомендации, существуют TODO: сделать через StreamAPI
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        for (SkillOffer skillOffer : skillOffers) {
            if (!skillRepository.existsByTitle(skillOffer.getSkill().getTitle())) {
                throw new DataValidationException("Skill " + skillOffer.getSkill().getTitle() + " doesn't exist");
                return Optional.empty();
            }
        }

        for (SkillOffer skillOffer : skillOffers) {
            skillOfferRepository.create(skillOffer.getSkill().getId(), skillOffer.getRecommendation().getId());
            // если у пользователя уже есть такой скилл, то добавить автора рекомендации гарантом к скиллу, если он ещё не стоит там
        }



    }
}
