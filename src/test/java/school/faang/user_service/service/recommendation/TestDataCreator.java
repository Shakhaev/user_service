package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.RecommendationRequestRcvDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

public class TestDataCreator {
    public static User createUser(long id, String title) {
        return User.builder()
                .id(id)
                .username(title)
                .build();
    }

    public static Skill createSkill(long id, String title) {
        return Skill.builder()
                .id(id)
                .title(title)
                .build();
    }

    public static SkillRequest createSkillRequest(long id, RecommendationRequest recommendationRequest, Skill skill) {
        return SkillRequest.builder()
                .id(id)
                .request(recommendationRequest)
                .skill(skill)
                .build();
    }

    public static RecommendationRequestRcvDto createRequestRcvDto(User requester,
                                                            User receiver,
                                                            RecommendationRequest request,
                                                            List<Long> skillIdsList) {
        return RecommendationRequestRcvDto.builder()
                .message(request.getMessage())
                .skillIds(skillIdsList)
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .build();
    }

    public static RecommendationRequest createRequest(Long id, User requester, User receiver, RequestStatus status) {
        return RecommendationRequest.builder()
                .id(id)
                .requester(requester)
                .receiver(receiver)
                .status(status)
                .createdAt(LocalDateTime.now())
                .message("Please confirm my skills")
                .build();
    }

    public static RejectionDto createRejectDto(String reason) {
        return RejectionDto.builder()
                .reason(reason)
                .build();
    }

    public static RequestFilterDto createFilterDto(RequestStatus status, Long requesterId, Long receiverId) {
        return RequestFilterDto.builder()
                .status(status)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();
    }
}
