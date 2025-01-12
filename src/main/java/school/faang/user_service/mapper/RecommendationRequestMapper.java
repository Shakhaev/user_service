package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestSaveDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    RecommendationRequest toEntity(RecommendationRequestSaveDto recommendationRequestSaveDto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skills", source = "skills")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    default List<String> toSkills(List<SkillRequest> skillRequests) {
        return skillRequests.stream()
                .map(SkillRequest::getSkill)
                .map(Skill::getTitle)
                .toList();
    }
}
