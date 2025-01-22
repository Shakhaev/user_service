package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.GetAllRecommendationsResponse;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationResponse;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationMapper recommendationMapper;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private final RecommendationValidator recommendationValidator;

    @Transactional
    public CreateRecommendationResponse create(CreateRecommendationRequest createRequest) {
        recommendationValidator.validateRecommendationContentIsNotEmpty(createRequest);
        checkLastRecommendationTime(createRequest.getAuthorId(), createRequest.getReceiverId(), createRequest.getCreatedAt());
        checkSkillsExist(createRequest.getSkillIds());

        Recommendation recommendation = recommendationMapper.fromCreateRequest(createRequest);
        User author = userRepository.getReferenceById(createRequest.getAuthorId());
        recommendation.setAuthor(author);
        User receiver = userRepository.getReferenceById(createRequest.getReceiverId());
        recommendation.setReceiver(receiver);
        List<Skill> skills = mapSkills(createRequest.getSkillIds());

        Long recommendationId = recommendationRepository.create(recommendation.getAuthor().getId(), recommendation.getReceiver().getId(), recommendation.getContent());
        recommendation.setId(recommendationId);
        saveSkillOffers(recommendation, skills);

        return recommendationMapper.toCreateResponse(recommendation);
    }

    @Transactional
    public UpdateRecommendationResponse update(UpdateRecommendationRequest updateRequest) {
        recommendationValidator.validateRecommendationContentIsNotEmpty(updateRequest);
        checkLastRecommendationTime(updateRequest.getAuthorId(), updateRequest.getReceiverId(), updateRequest.getCreatedAt());
        checkSkillsExist(updateRequest.getSkillIds());

        Recommendation recommendation = recommendationMapper.fromUpdateRequest(updateRequest);
        User author = userRepository.getReferenceById(updateRequest.getAuthorId());
        recommendation.setAuthor(author);
        User receiver = userRepository.getReferenceById(updateRequest.getReceiverId());
        recommendation.setReceiver(receiver);
        List<Skill> skills = mapSkills(updateRequest.getSkillIds());

        recommendationRepository.update(recommendation.getAuthor().getId(), recommendation.getReceiver().getId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        saveSkillOffers(recommendation, skills);

        return recommendationMapper.toUpdateResponse(recommendation);
    }

    @Transactional
    public void delete(long id) {
        skillOfferRepository.deleteAllByRecommendationId(id);
        recommendationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<GetAllRecommendationsResponse> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        return recommendationPage.get()
                .map(recommendationMapper::toGetAllResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetAllRecommendationsResponse> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        return recommendationPage.get()
                .map(recommendationMapper::toGetAllResponse)
                .toList();
    }

    private void saveSkillOffers(Recommendation recommendation, List<Skill> skills) {
        skills.forEach(skill -> {
            Long skillOfferId = skillOfferRepository.create(skill.getId(), recommendation.getId());
            recommendation.addSkillOffer(skillOfferRepository.findById(skillOfferId).orElseThrow());
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

    private List<Skill> mapSkills(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    private void checkLastRecommendationTime(Long authorId, Long receiverId, LocalDateTime createdAt) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId)
                .ifPresent(lastRecommendation ->
                        recommendationValidator.validateLastRecommendationTime(lastRecommendation, createdAt));
    }

    private void checkSkillsExist(List<Long> skillIds) {
        skillIds.forEach(s -> {
            if (!skillRepository.existsById(s)) {
                throw new DataValidationException("Skill with ID = " + s + " doesn't exist");
            }
        });
    }
}
