package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {
    RecommendationRequestDto toDto(RecommendationRequest entity);
    RecommendationRequest toEntity(RecommendationRequestDto dto);


}
