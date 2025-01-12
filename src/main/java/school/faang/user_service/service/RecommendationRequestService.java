package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestRcvDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RecommendationRequestService {
    private static final int REQUEST_PERIOD_OF_THE_SAME_USER = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper mapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;

    public RecommendationRequestDto create(RecommendationRequestRcvDto requestDto) {
        validateRecommendationRequest(requestDto);
        RecommendationRequest request = convertRequestDtoToEntity(requestDto);
        RecommendationRequest requestSaved = recommendationRequestRepository.save(request);
        List<SkillRequest> skills = requestDto.getSkillIds().stream()
                .map(skillId -> skillRequestRepository.create(requestSaved.getId(), skillId))
                .toList();
        requestSaved.setSkills(skills);
        return mapper.toDto(requestSaved);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        return recommendationRequestRepository.findAll().stream()
                .filter(request -> filter.getStatus() == null || request.getStatus() == filter.getStatus())
                .filter(request -> filter.getRequesterId() == null || filter.getRequesterId().equals(
                        request.getRequester() != null ? request.getRequester().getId() : null))
                .filter(request -> filter.getReceiverId() == null || filter.getReceiverId().equals(
                        request.getReceiver() != null ? request.getReceiver().getId() : null))
                .map(mapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        return mapper.toDto(recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found")));
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request id " + id + " not found"));
        validateRequestForReject(request);
        request.setStatus(RequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());
        request.setRejectionReason(rejectionDto.getReason());
        return mapper.toDto(recommendationRequestRepository.save(request));
    }

    private void validateRequestForReject(RecommendationRequest request) {
        long id = request.getId();
        if (request.getStatus().equals(RequestStatus.ACCEPTED)) {
                throw new IllegalArgumentException("The recommendation request id " + id + " is already accepted");
        }
        if (request.getStatus().equals(RequestStatus.REJECTED)) {
                throw new IllegalArgumentException("The recommendation request id " + id + " is already rejected");
        }
    }

    private RecommendationRequest convertRequestDtoToEntity(RecommendationRequestRcvDto requestDto) {
        RecommendationRequest request = mapper.toEntity(requestDto);
        User requester = getUserById(requestDto.getRequesterId());
        User receiver = getUserById(requestDto.getReceiverId());
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        return request;
    }

    private void validateRecommendationRequest(RecommendationRequestRcvDto request) {
        Optional<RecommendationRequest> lastRequest = recommendationRequestRepository.findLatestPendingRequest(
                request.getRequesterId(), request.getReceiverId());

        if (lastRequest.isPresent()) {
            LocalDateTime lastRequestDate = lastRequest.get().getCreatedAt();
            if (lastRequestDate.isAfter(LocalDateTime.now().minusMonths(REQUEST_PERIOD_OF_THE_SAME_USER))) {
                throw new IllegalArgumentException("Recommendation request must be sent once in "
                        + REQUEST_PERIOD_OF_THE_SAME_USER + " months");
            }
        }
        for (long id : request.getSkillIds()) {
            if (!skillRepository.existsById(id)) {
                throw new IllegalArgumentException("Skill with id = " + id + " not exist");
            }
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with id %s not found", id)));
    }
}
