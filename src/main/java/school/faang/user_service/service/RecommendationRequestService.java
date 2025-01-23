package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.response.RecommendationResponseDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation_request.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper mapper;
    private final RecommendationRequestValidator validator;
    private final List<RecommendationRequestFilter> requestFilters;

    @Transactional
    public RecommendationResponseDto createRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = mapper.toEntity(recommendationRequestDto);

        long requesterId = recommendationRequestDto.getRequesterId();
        long receiverId = recommendationRequestDto.getReceiverId();
        List<Long> skillIds = recommendationRequestDto.getSkillIds();

        validator.checkUsersExist(requesterId, receiverId);
        validator.checkRequestWithinSixMonthsExist(requesterId, receiverId);
        validator.checkAllSkillsExist(skillIds);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.
                create(requesterId, receiverId, recommendationRequest.getMessage());
        long requestId = savedRecommendationRequest.getId();

        skillIds.forEach(skillId -> skillRequestRepository.create(requestId, skillId));

        return mapper.toDto(savedRecommendationRequest);
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponseDto> getRecommendationRequests(RecommendationRequestFilterDto filters) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();
        for (RecommendationRequestFilter requestFilter : requestFilters) {
            if (requestFilter.isApplicable(filters)) {
                requests = requestFilter.apply(requests, filters);
            }
        }
        return requests
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecommendationResponseDto getRecommendationRequest(long recommendationRequestId) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(recommendationRequestId);
        validator.checkRecommendationRequestExists(request);

        return mapper.toDto(request.get());
    }

    @Transactional
    public RecommendationResponseDto rejectRecommendationRequest(long recommendationRequestId,
                                                                 RecommendationRejectionDto rejection) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(recommendationRequestId);
        validator.checkRecommendationRequestExists(request);

        RecommendationRequest recommendationRequest = request.get();
        validator.validateRecommendationRequestStatus(recommendationRequest);

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());
        recommendationRequestRepository.save(recommendationRequest);

        return mapper.toDto(recommendationRequest);
    }
}
