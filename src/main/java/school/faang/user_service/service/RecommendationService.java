package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final static int PERIOD_TO_ADD_NEW_RECOMMENDATION = 6;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validateRecommendationForPeriod(recommendationDto);
        validateSkillsInSystem(recommendationDto);

        Recommendation recommendation = recommendationRepository
                .save(createRecommendationEntityFromDto(recommendationDto));

        saveNewSkillOffers(recommendation);
        recommendationRepository.create(
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent()
        );

        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        validateRecommendationForPeriod(recommendationDto);
        validateSkillsInSystem(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );
        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.getId());
        updateSkillOffers(recommendationDto);

        return recommendationDto;
    }

    public void delete(Long recommendationId) {
        validateRecommendationExistsById(recommendationId);
        recommendationRepository.deleteById(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> allByReceiverId = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());
        List<Recommendation> recommendations = allByReceiverId.getContent();

        return recommendationMapper.toDtoList(recommendations);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> allByAuthorId = recommendationRepository
                .findAllByAuthorId(authorId, Pageable.unpaged());
        List<Recommendation> recommendations = allByAuthorId.getContent();

        return recommendationMapper.toDtoList(recommendations);
    }

    private void validateRecommendationForPeriod(RecommendationDto recommendationDto) {
        Optional<LocalDateTime> lastRecommendationDate = getLastRecommendationDate(
                recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        lastRecommendationDate.ifPresent(date -> {
            if (date.isAfter(LocalDateTime.now().minusMonths(PERIOD_TO_ADD_NEW_RECOMMENDATION))) {
                throw new BusinessException(
                        String.format("Новая рекомендация может быть дана не ранее, чем через %s месяцев",
                                PERIOD_TO_ADD_NEW_RECOMMENDATION));
            }
        });
    }

    private void validateRecommendationExistsById(Long recommendationId) {
        if (!recommendationRepository.existsById(recommendationId)) {
            throw new DataValidationException("Рекомендация с id:" + recommendationId + " не найдена в системе");
        }
    }

    private void validateSkillsInSystem(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers().forEach(skillOffer -> {
            if (!skillRepository.existsById(skillOffer.getSkillId())){
                throw new DataValidationException("Вы предлагаете навыки, которых нет в системе");
            }
        });
    }

    private Optional<LocalDateTime> getLastRecommendationDate(Long authorId, Long receiverId) {
        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
        if (lastRecommendation.isPresent()) {
            LocalDateTime lastRecommendationDate = lastRecommendation.get().getCreatedAt();
            return Optional.ofNullable(lastRecommendationDate);
        } else {
            return Optional.empty();
        }
    }

    private Recommendation createRecommendationEntityFromDto(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation.setAuthor(userRepository.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new DataValidationException(String.format("Автора с id: %s не существует",
                        recommendationDto.getAuthorId()))));

        recommendation.setReceiver(userRepository.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException(String.format("Получателя рекомендации с id: %s не существует",
                        recommendationDto.getReceiverId()))));
        recommendation.setSkillOffers(skillOfferRepository.findAllByUserId(recommendationDto.getReceiverId()));

        return recommendation;
    }

    private void saveNewSkillOffers(Recommendation recommendation) {
        validateRecommendationExistsById(recommendation.getId());
        recommendation.getSkillOffers().forEach(skillOffer ->
                skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId())
        );
    }

    private void updateSkillOffers(RecommendationDto recommendationDto) {
        validateRecommendationExistsById(recommendationDto.getId());
        recommendationDto.getSkillOffers().forEach(skillOffer ->
                skillOfferRepository.create(skillOffer.getSkillId(), recommendationDto.getId())
        );
    }
}
