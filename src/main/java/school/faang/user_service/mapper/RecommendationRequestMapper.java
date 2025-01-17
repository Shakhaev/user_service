package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestResponseDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    RecommendationRequestResponseDto toResponseDto(RecommendationRequest entity);

    List<RecommendationRequestResponseDto> toResponseDtoList(List<RecommendationRequest> entities);
}