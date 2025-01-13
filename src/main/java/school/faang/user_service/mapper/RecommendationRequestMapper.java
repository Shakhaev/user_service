package school.faang.user_service.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.respones.RecommendationResponseDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillsRequests", target = "skillsRequests", qualifiedByName = "mapSkillsRequests")
    RecommendationResponseDto toDto(RecommendationRequest recommendationRequest);

    @Named("mapSkillsRequests")
    default List<Long> mapSkillsRequests(List<SkillRequest> skillRequests) {
        if (skillRequests == null) {
            return null;
        }
        return skillRequests.stream()
            .map(SkillRequest::getId)
            .toList();
    }
}
