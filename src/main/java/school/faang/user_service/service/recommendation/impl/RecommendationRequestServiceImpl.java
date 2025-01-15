package school.faang.user_service.service.recommendation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestResponseDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.RecommendationRequestCreatedException;
import school.faang.user_service.exception.RequestAlreadyProcessedException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.service.recommendation.SkillRequestService;
import school.faang.user_service.service.recommendation.UserService;
import school.faang.user_service.service.recommendation.util.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserService userService;
    private final SkillRequestService skillRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestFilter recommendationRequestFilter;

    @Transactional
    @Override
    public RecommendationRequestResponseDto create(RecommendationRequestCreateDto recommendationRequestCreateDto) {
        long requesterId = recommendationRequestCreateDto.requesterId();
        long receiverId = recommendationRequestCreateDto.receiverId();
        User requester = userService.findById(requesterId);
        User receiver = userService.findById(receiverId);
        isSixMonthLeft(requesterId, receiverId);
        RecommendationRequest mappedRecommendationRequest
                = recommendationRequestMapper.toEntity(recommendationRequestCreateDto, requester, receiver);
        RecommendationRequest recommendationRequest = recommendationRequestRepository.save(mappedRecommendationRequest);
        List<SkillRequest> skillRequests
                = skillRequestService.createSkillRequests(recommendationRequest.getId(), recommendationRequestCreateDto.skills());
        recommendationRequest.setSkills(skillRequests);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    @Override
    public List<RecommendationRequestResponseDto> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll();
        Predicate<RecommendationRequest> predicate = recommendationRequestFilter.getPredicates(filter)
                .stream()
                .reduce(Predicate::and)
                .orElse(request -> true);
        return recommendationRequests.stream()
                .filter(predicate)
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    @Override
    public RecommendationRequestResponseDto getRequest(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.recommendationNotFoundException(id));
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    @Transactional
    @Override
    public void rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.recommendationNotFoundException(id));
        if (recommendationRequest.getStatus().equals(RequestStatus.REJECTED) ||
                recommendationRequest.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new RequestAlreadyProcessedException("The request has already been processed");
        }
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.reason());
    }

    private void isSixMonthLeft(long requesterId, long receiverId) {
        recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId)
                .ifPresent(lastRequest -> {
                    boolean isLeft = lastRequest.getCreatedAt()
                            .plusMonths(6)
                            .isBefore(LocalDateTime.now());
                    if (!isLeft) {
                        throw new RecommendationRequestCreatedException("request can be submitted once every 6 month");
                    }
                });
    }
}
