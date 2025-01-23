package school.faang.user_service.dto.recommendation;

import school.faang.user_service.entity.RequestStatus;

import java.util.List;

public record CreateRecommendationRequestResponse(long id,
                                                  String message,
                                                  RequestStatus status,
                                                  List<String> skills,
                                                  long requesterId,
                                                  long receiverId) {
}
