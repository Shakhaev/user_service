package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestedEventMapper {
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationRequestedEvent recommendationRequestToEvent(RecommendationRequest recommendationRequest);
}
