package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.user.UserSkillGuaranteeDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor

public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillOffer skillOffer;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final RecommendationMapper recommendationMapper;
    private final DataValidationException dataValidationException;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validateMonthsBetweenRecommendations(recommendationDto);
        validateSkillOffers(recommendationDto);

        UserSkillGuaranteeDto userSkillGuaranteeDto = new UserSkillGuaranteeDto();

        userSkillGuaranteeDto.setSkillId(skillOffer.getSkill().getId());
        userSkillGuaranteeDto.setGuarantorId(recommendationDto.getAuthorId());
        userSkillGuaranteeDto.setUserId(recommendationDto.getReceiverId());
        userSkillGuaranteeRepository.save(userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto));

        Long recommendationId = recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        recommendationDto.setId(recommendationId);
        return recommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        validateMonthsBetweenRecommendations(recommendation);
        validateSkillOffers(recommendation);

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        recommendation.getSkillOffers().forEach(skillOfferDto -> skillOfferRepository.create(skillOfferDto.getSkillId(), recommendation.getId()));
        return recommendation;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());

        return recommendations.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId);
        return recommendations.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }


    public void validateMonthsBetweenRecommendations(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto
                        .getReceiverId()).orElseThrow(()
                        -> new DataValidationException("Рекомендация не найдена"));
        LocalDateTime localDateTime = LocalDateTime.now();
        if (!localDateTime.isAfter(recommendation.getCreatedAt().plusMonths(6))) {
            throw new DataValidationException(
                    "Рекомендация может быть дана не ранее, чем через 6 месяцев после последней.");
        }
    }

    public void validateSkillOffers(RecommendationDto recommendationDto) {
        for (SkillOfferDto skillOffer : recommendationDto.getSkillOffers()) {
            if (!recommendationRepository.existsById(skillOffer.getSkillId())) {
                skillOfferRepository.create(skillOffer.getSkillId(), recommendationDto.getId());
            }
        }
    }

    //    public List<UserDto> getPremiumUsers(UserFilterDto filter) {
//        List<User> premiumUsers = userRepository.findPremiumUsers().toList();
//        if (!userFilters.isEmpty()) {
//            userFilters.stream()
//                    .filter(userFilter -> userFilter.isApplicable(filter))
//                    .forEach(userFilter -> userFilter.apply(premiumUsers, filter));
//        }
//
//       return userMapper.toDto(premiumUsers);
//
//
//    }
}
