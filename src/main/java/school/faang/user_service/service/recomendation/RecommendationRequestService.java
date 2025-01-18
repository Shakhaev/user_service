package school.faang.user_service.service.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recomendation.FilterRecommendationRequestsDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestRejectException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recomendation.filters.RecommendationRequestFilter;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RecommendationRequestFilter> filters;
    private final RecommendationRequestValidator validator;

    @Transactional
    public RecommendationRequest create(RecommendationRequest recommendationRequest, List<Long> skillIds) {
        validator.validateCreateRecommendationRequest(recommendationRequest, skillIds);

        recommendationRequest.setStatus(RequestStatus.PENDING);
        RecommendationRequest savedRequest = recommendationRequestRepository.save(recommendationRequest);

        skillIds.forEach((skillId) -> skillRequestRepository.create(savedRequest.getId(), skillId));

        return savedRequest;
    }

    @Transactional(readOnly = true)
    public List<RecommendationRequest> getRecommendationRequests(
            FilterRecommendationRequestsDto filterRecommendationRequestsDto) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();

        return filters.stream()
                .filter(filter -> filter.isApplicable(filterRecommendationRequestsDto))
                .reduce(requests,
                        (stream, filter) -> filter.apply(stream, filterRecommendationRequestsDto),
                        (s1, s2) -> s1)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecommendationRequest findRequestById(Long id) {
        return recommendationRequestRepository
                .findById(id)
                .orElseThrow(() -> new RecommendationRequestNotFoundException(id));
    }

    @Transactional
    public RecommendationRequest rejectRequest(RecommendationRequest rejection) {
        RecommendationRequest recommendationRequest = findRequestById(rejection.getId());

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new RecommendationRequestRejectException(RequestStatus.PENDING);
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getRejectionReason());

        return recommendationRequestRepository.save(recommendationRequest);
    }
}
