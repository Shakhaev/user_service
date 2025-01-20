package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
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
    public RecommendationRequestDto createRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = mapper.toEntity(recommendationRequestDto);

        long requesterId = recommendationRequestDto.getRequesterId();
        long receiverId = recommendationRequestDto.getReceiverId();
        List<Long> skillIds = recommendationRequestDto.getSkillIds();

        validator.checkUsersExist(requesterId, receiverId);
        validator.checkRequestWithinSixMonthsExist(requesterId, receiverId);
        validator.checkAllSkillsExist(skillIds);

        recommendationRequest = recommendationRequestRepository.create(requesterId, receiverId,
                recommendationRequest.getMessage());
        long requestId = recommendationRequest.getId();

        skillIds.forEach(skillId -> skillRequestRepository.create(requestId, skillId));

        return mapper.toDto(recommendationRequest);
    }

    @Transactional(readOnly = true)
    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filters) {
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
    public RecommendationRequestDto getRecommendationRequest(long id) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(id);
        validator.checkRecommendationRequestExists(request);

        return mapper.toDto(request.get());
    }

    @Transactional
    public RecommendationRequestDto rejectRecommendationRequest(long id, RejectionDto rejection) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(id);
        validator.checkRecommendationRequestExists(request);

        RecommendationRequest recommendationRequest = request.get();
        validator.validateRecommendationRequestStatus(recommendationRequest);

        recommendationRequest.setRejectionReason(rejection.getReason());
        recommendationRequestRepository.save(recommendationRequest);

        return mapper.toDto(recommendationRequest);
    }
}
