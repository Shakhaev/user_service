package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skill.SkillRequestServiceImpl;
import school.faang.user_service.service.skill.SkillServiceImpl;
import school.faang.user_service.service.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    @Value("${recommendation-request.min-month}")
    private int minMonth;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserServiceImpl userService;
    private final SkillServiceImpl skillService;
    private final SkillRequestServiceImpl skillRequestService;

    @Override
    public RecommendationRequest create(RecommendationRequestDto dto) throws IllegalArgumentException {
        Set<Long> userIds = dto.getRequesterIdAndReceiverIds();

        checkUsersExist(userIds);
        checkPeriod(dto, userIds);
        checkSkillsExist(dto);

        RecommendationRequest recommendationRequest = recommendationRequestRepository.save(
                recommendationRequestMapper.update(recommendationRequestMapper.toEntity(dto),
                        RecommendationRequest.builder()
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .status(RequestStatus.PENDING)
                                .build())
        );

        recommendationRequest.setSkills(skillRequestService.createAllSkillRequest(dto.getSkills(), recommendationRequest));
        return recommendationRequest;
    }

    @Override
    public List<RecommendationRequest> getRequestByFilter(RequestFilterDto filter) {
        return recommendationRequestRepository.findAll().stream()
                .filter(request -> recommendationRequestMapper.requestToRequestFilterDto(request).equals(filter))
                .toList();
    }

    @Override
    public RecommendationRequest getRequestById(Long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Request not found"));
    }

    private void checkUsersExist(Set<Long> userIds) {
        if (userService.findByIds(userIds).size() != userIds.size()) {
            throw new IllegalArgumentException("Requester or receiver not found");
        }
    }

    private void checkSkillsExist(RecommendationRequestDto dto) {
        if (!skillService.checkSkillsExist(dto.getSkills())) {
            throw new IllegalArgumentException("Skills not found");
        }
    }

    private void checkPeriod(RecommendationRequestDto dto, Set<Long> userIds) {
        if (userIds.size() == 1) {
            recommendationRequestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId())
                    .ifPresent(recommendationRequest -> {
                        if (recommendationRequest.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(minMonth))) {
                            throw new IllegalArgumentException("Less than min months have passed since the previous request");
                        }
                    });
        }
    }
}
