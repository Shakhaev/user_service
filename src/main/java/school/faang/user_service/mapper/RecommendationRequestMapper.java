package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "requester.id", source = "requesterId")
    @Mapping(target = "receiver.id", source = "receiverId")
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(target = "id", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skills", expression = "java(getSkillIds(request))")
    RecommendationRequestDto toDto(RecommendationRequest request);

    @Mapping(target = "skillIds", expression = "java(getSkillIds(request))")
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RequestFilterDto requestToRequestFilterDto(RecommendationRequest request);

    RecommendationRequest update(@MappingTarget RecommendationRequest target, RecommendationRequest source);

    default List<Long> getSkillIds(RecommendationRequest request) {
        if (request == null) {
            return List.of();
        }
        return request.getSkills().stream().map(SkillRequest::getId).toList();
    }
}
