package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.dto.recommendation.request.filter.RecommendationRequestFilter;
import school.faang.user_service.dto.recommendation.request.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.properties.UserServiceProperties;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skill.SkillRequestService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final UserServiceProperties userServiceProperties;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestService skillRequestService;
    private final SkillRequestMapper skillRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    @Override
    public RecommendationRequest create(RecommendationRequestDto dto) {
        checkRecommendationRequestDtoToSave(dto);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);

        recommendationRequest = recommendationRequestRepository.save(
                recommendationRequestMapper.update(recommendationRequest, RecommendationRequest.builder()
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .status(RequestStatus.PENDING)
                        .build())
        );

        List<SkillRequestDto> savedSkillRqs = skillRequestService.createAllSkillRequest(dto.getSkills(), recommendationRequest);
        recommendationRequest.setSkills(savedSkillRqs.stream().map(skillRequestMapper::toEntity).toList());
        return recommendationRequest;
    }

    @Override
    public List<RecommendationRequest> getRequestByFilter(RecommendationRequestFilterDto dto) {
        AtomicReference<List<RecommendationRequest>> recommendationRequests = new AtomicReference<>(recommendationRequestRepository.findAll());
        recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(dto))
                .forEach(filter -> recommendationRequests.set(filter.apply(recommendationRequests.get(), dto)));
        return recommendationRequests.get();
    }

    @Override
    public RecommendationRequest getRequestById(Long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("RecommendationRequest not found"));
    }

    private void checkRecommendationRequestDtoToSave(RecommendationRequestDto dto) {
        Set<Long> userIds = new HashSet<>(List.of(dto.getRequesterId(), dto.getReceiverId()));

        userIds.stream()
                .filter(userId -> !userRepository.existsById(userId))
                .forEach(userId -> {
                    throw new EntityNotFoundException("User with id " + userId + " not found");
                });

        if (userIds.size() == 1 &&
                recommendationRequestRepository.findLatestPendingRequestCreatedAfterThen(
                        dto.getRequesterId(),
                        LocalDateTime.now().minusMonths(userServiceProperties.getRecommendationRequest().getMinMonth()))) {
            throw new IllegalArgumentException("Less than min months have passed since the previous request");
        }

        if (dto.getSkills().stream().noneMatch(skillRepository::existsById)) {
            throw new EntityNotFoundException("Skills not found");
        }
    }
}
