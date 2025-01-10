package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("api/recommendations")
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    // Создание нового запроса рекомендации
    @PostMapping("/request")
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(
            @RequestBody RecommendationRequestDto recommendationRequest) {

        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        RecommendationRequestDto createdRequest = recommendationRequestService.create(recommendationRequest);
        return ResponseEntity.ok(createdRequest);
    }

    // Получение списка рекомендаций по фильтру
    @GetMapping("/requests")
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(
            @RequestBody RequestFilterDto filter) {
        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(filter);
        return ResponseEntity.ok(requests);
    }

    // Получение конкретного запроса по ID
    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@PathVariable long id) {
        RecommendationRequestDto recommendationRequest = recommendationRequestService.getRequest(id);
        return ResponseEntity.ok(recommendationRequest);
    }

    // Отклонение запроса рекомендации
    @PatchMapping("/{id}/reject")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(@PathVariable long id,
                                                                  @RequestBody RejectionDto rejection) {

        if (rejection.getRejectionReason() == null || rejection.getRejectionReason().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        RecommendationRequestDto rejectRequest = recommendationRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok(rejectRequest);
    }
}
