package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationRequestResponseDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "receiver", source = "receiver")
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    RecommendationRequest toEntity(RecommendationRequestCreateDto recommendationRequestCreateDto, User requester, User receiver);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skills", source = "skills")
    RecommendationRequestResponseDto toDto(RecommendationRequest recommendationRequest);

    default List<String> toSkills(List<SkillRequest> skillRequests) {
        return skillRequests.stream()
                .map(SkillRequest::getSkill)
                .map(Skill::getTitle)
                .toList();
    }
}
