package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.user.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.SkillRepository;
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
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;

    public RecommendationDto create(RecommendationDto recommendation) {
       validateMonthsBetweenRecommendations(recommendation);
        for (SkillOfferDto skillOfferDto : recommendation.getSkillOffers()) {
            if (!skillRepository.existsById(skillOfferDto.getSkillId())) {
                throw new DataValidationException("Skill with id " + skillOfferDto.getSkillId() + " not found");
            }
        }
        List<Skill> skills = skillRepository.findAllByUserId(recommendation.getReceiverId());
        for (Skill skill : skills) {
            if (skills.contains(skill)) {
                UserSkillGuaranteeDto userSkillGuaranteeDto = new UserSkillGuaranteeDto();
                userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto);
                userSkillGuaranteeRepository.save(userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto));
            }
        }
        Long recommendationId = recommendationRepository.create(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        recommendation.getSkillOffers().forEach(skillOfferDto -> skillOfferRepository.create(skillOfferDto.getSkillId(), recommendationId));
        return recommendationMapper.toDto(recommendationRepository.findById(recommendationId).orElseThrow());
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        validateMonthsBetweenRecommendations(recommendationDto);
        for (SkillOfferDto skillOffer : recommendationDto.getSkillOffers()) {
            if (!recommendationRepository.existsById(skillOffer.getSkillId())) {
                skillOfferRepository.create(skillOffer.getSkillId(), recommendationDto.getId());
            }
        }
        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.getId());
        recommendationDto.getSkillOffers().forEach(skillOfferDto -> skillOfferRepository.create(skillOfferDto.getSkillId(), recommendationDto.getId()));
        return recommendationDto;
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
    private void validateMonthsBetweenRecommendations(RecommendationDto recommendationDto) {
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

    private void validateSkillOffers(RecommendationDto recommendationDto) {
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
