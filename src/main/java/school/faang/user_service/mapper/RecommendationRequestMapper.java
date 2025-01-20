package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkillRequestIds")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("mapSkillRequestIds")
    default List<Long> mapSkillRequestIds(List<SkillRequest> skillRequests) {
        return skillRequests.stream().map(SkillRequest::getId).toList();
    }
}
