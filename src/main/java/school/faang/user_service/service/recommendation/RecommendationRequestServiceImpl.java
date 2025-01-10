package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.SkillRequestService;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    @Value("${recommendation-request.min-month}")
    private int minMonth;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillMapper skillMapper;
    private final UserService userService;
    private final SkillService skillService;
    private final SkillRequestService skillRequestService;

    @Override
    public RecommendationRequest create(RecommendationRequestDto dto) throws IllegalArgumentException {
        if (userService.findByIds(List.of(dto.getReceiverId(), dto.getRequesterId())).size() != 2) {
            throw new IllegalArgumentException("Requester or receiver not found");
        }

        recommendationRequestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId())
                .ifPresent(recommendationRequest -> {
                    if (recommendationRequest.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(minMonth))) {
                        throw new IllegalArgumentException("Less than min months have passed since the previous request");
                    }
                });

        if (!skillService.checkSkillsExist(dto.getSkills().stream().map(skillMapper::toEntity).toList())) {
            throw new IllegalArgumentException("Skills not found");
        }

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);

        skillRequestService.createAllSkillRequest(dto.getSkills(), recommendationRequest);
        return recommendationRequestRepository.save(recommendationRequest);
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
}
