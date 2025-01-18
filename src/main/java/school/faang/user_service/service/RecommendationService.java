package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.GetAllGivenRecommendationsResponse;
import school.faang.user_service.dto.recommendation.GetAllUserRecommendationsResponse;
import school.faang.user_service.dto.recommendation.RecommendationDto;
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

import java.time.Period;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationMapper recommendationMapper;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public CreateRecommendationResponse create(CreateRecommendationRequest recommendationRequest) {
        validateRecommendation(recommendationRequest);

        Recommendation recommendation = mapRecommendation(recommendationRequest);
        List<Skill> skills = mapSkills(recommendationRequest.getSkillIds());

        Long recommendationId = recommendationRepository.create(recommendation.getAuthor().getId(), recommendation.getReceiver().getId(), recommendation.getContent());
        recommendation.setId(recommendationId);
        saveSkillOffers(recommendation, skills);

        return (CreateRecommendationResponse) recommendationMapper.toDto(recommendation);
    }

    public UpdateRecommendationResponse update(UpdateRecommendationRequest recommendationRequest) {
        validateRecommendation(recommendationRequest);

        Recommendation recommendation = mapRecommendation(recommendationRequest);
        List<Skill> skills = mapSkills(recommendationRequest.getSkillIds());

        recommendationRepository.update(recommendation.getAuthor().getId(), recommendation.getReceiver().getId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        saveSkillOffers(recommendation, skills);

        return (UpdateRecommendationResponse) recommendationMapper.toDto(recommendation);
    }

    public void delete(long id) {
        skillOfferRepository.deleteAllByRecommendationId(id);
        recommendationRepository.deleteById(id);
    }

    public List<GetAllUserRecommendationsResponse> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        return recommendationPage.get()
                .map(recommendationMapper::toDto)
                .map(r -> (GetAllUserRecommendationsResponse)r)
                .toList();
    }

    public List<GetAllGivenRecommendationsResponse> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        return recommendationPage.get()
                .map(recommendationMapper::toDto)
                .map(r -> (GetAllGivenRecommendationsResponse)r)
                .toList();
    }

    private void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent().isBlank())
            throw new DataValidationException("Recommendation content is empty");

        Optional<Recommendation> recommendationOptional =  recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        if (recommendationOptional.isPresent()) {
            Recommendation lastRecommendation = recommendationOptional.get();

            Period timeDifference = Period.between(lastRecommendation.getCreatedAt().toLocalDate(), now().toLocalDate());

            if (timeDifference.getMonths() < 6) {
                throw new DataValidationException("The author can make a recommendation to the user no earlier than 6 months after the last recommendationDto. The last recommendationDto was given " + lastRecommendation.getCreatedAt());
            }
        }

        recommendationDto.getSkillOfferIds().forEach(s -> {
            if (!skillRepository.existsById(s)) {
                throw new DataValidationException("Skill with ID = " + s + " doesn't exist");
            }
        });
    }
    private Recommendation mapRecommendation(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        User author = userRepository.getReferenceById(recommendationDto.getAuthorId());
        recommendation.setAuthor(author);
        User receiver = userRepository.getReferenceById(recommendationDto.getReceiverId());
        recommendation.setReceiver(receiver);
        return recommendation;
    }

    private List<Skill> mapSkills(List<Long> skillIds) {
        return skillIds.stream().map(skillRepository::getReferenceById).toList();
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
}
