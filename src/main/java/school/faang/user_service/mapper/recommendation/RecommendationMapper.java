package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;

import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    Recommendation toEntity(RecommendationDto recommendationDto);

    RecommendationDto toDto(Recommendation recommendation);
}
