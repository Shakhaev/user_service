package school.faang.user_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.request.RejectionDto;
import school.faang.user_service.dto.request.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestValidator recommendationValidator;
    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters;

    @Transactional
    public RecommendationRequest create(Long requesterId, Long receiverId, String message,
                                        List<Long> skillIds) {
        recommendationValidator.validateUserExistence(requesterId);
        recommendationValidator.validateUserExistence(receiverId);
        recommendationValidator.validateSkillsExist(skillIds);

        final RecommendationRequest[] result = new RecommendationRequest[1];
        Optional<RecommendationRequest> latestRecommendationRequest =
            recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);

        latestRecommendationRequest.ifPresentOrElse(rec -> {
                LocalDateTime nowAfterAccept = LocalDateTime.now().minusMonths(6);

                if (rec.getCreatedAt().isBefore(nowAfterAccept)) {
                    rec.setStatus(RequestStatus.PENDING);
                    rec.setCreatedAt(LocalDateTime.now());
                    rec.setUpdatedAt(LocalDateTime.now());
                    rec.setSkillsRequests(createSkillRequests(rec, skillIds));
                    result[0] = recommendationRequestRepository.save(rec);
                } else {
                    throw new IllegalArgumentException(
                        "Рекомендация уже существует и не прошло 6 месяцев с момента последнего запроса.");
                }
            },
            () -> {
                RecommendationRequest rec = new RecommendationRequest();
                rec.setMessage(message);
                rec.setRequester(userRepository.findById(requesterId).orElseThrow());
                rec.setReceiver(userRepository.findById(receiverId).orElseThrow());
                rec.setSkillsRequests(createSkillRequests(rec, skillIds));
                rec.setStatus(RequestStatus.PENDING);
                result[0] = recommendationRequestRepository.save(rec);
            }
        );

        return result[0];
    }

    public List<RecommendationRequest> getAllRequests(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> allRecommendation =
            recommendationRequestRepository.findAll().stream();
        return filters.stream()
            .filter(f -> f.isApplicable(filterDto))
            .reduce(allRecommendation, (stream, filter) ->
                filter.apply(stream, filterDto), (s1, s2) -> s1)
            .toList();
    }

    public RecommendationRequest getRequestById(Long id) {
        return recommendationRequestRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Not Found: " + id));
    }

    public RecommendationRequest rejectRequest(Long recommendationRequestId,
                                               RejectionDto rejectionDto) {
        RecommendationRequest entity = getRequestById(recommendationRequestId);
        if (entity.getStatus() == RequestStatus.PENDING) {
            entity.setStatus(RequestStatus.REJECTED);
            entity.setRejectionReason(rejectionDto.getReason());
            entity.setUpdatedAt(LocalDateTime.now());
            return recommendationRequestRepository.save(entity);
        }

        return entity;
    }


    private List<SkillRequest> createSkillRequests(RecommendationRequest recommendationRequest,
                                                   List<Long> skillIds) {
        return skillIds.stream()
            .map(skillId -> SkillRequest.builder()
                .request(recommendationRequest)
                .skill(skillRepository.findById(skillId).orElseThrow())
                .build()
            )
            .toList();
    }
}
