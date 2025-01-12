package school.faang.user_service.service.recommendation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestSaveDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.RecommendationRequestCreatedException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.service.recommendation.SkillService;
import school.faang.user_service.service.recommendation.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    @Transactional
    @Override
    public RecommendationRequestDto create(RecommendationRequestSaveDto recommendationRequestSaveDto) {
        long requesterId = recommendationRequestSaveDto.requesterId();
        long receiverId = recommendationRequestSaveDto.receiverId();
        User requester = userService.findById(requesterId);
        User receiver = userService.findById(receiverId);
        isSixMonthLeft(requesterId, receiverId);
        List<Skill> skills = recommendationRequestSaveDto.skills().stream()
                .map(skillService::findById)
                .toList();

        RecommendationRequest mappedRecommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestSaveDto);
        mappedRecommendationRequest.setRequester(requester);
        mappedRecommendationRequest.setReceiver(receiver);

        RecommendationRequest recommendationRequest = recommendationRequestRepository.save(mappedRecommendationRequest);
        List<SkillRequest> skillRequests = skills.stream()
                .map(skill -> skillRequestRepository.create(recommendationRequest.getId(), skill.getId()))
                .toList();
        if(recommendationRequest.getSkills() == null) {
            recommendationRequest.setSkills(skillRequests);
        } else {
            skillRequests.forEach(recommendationRequest::addSkillRequest);
        }
        return recommendationRequestMapper.toDto(recommendationRequest);
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
