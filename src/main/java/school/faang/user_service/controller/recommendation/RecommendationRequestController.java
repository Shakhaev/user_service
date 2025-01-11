package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
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

    @PostMapping("/request")
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(
            @Valid @RequestBody RecommendationRequestDto recommendationRequest) {

        RecommendationRequestDto createdRequest = recommendationRequestService.create(recommendationRequest);
        return ResponseEntity.ok(createdRequest);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(
            @Valid @RequestBody RequestFilterDto filter) {
        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(filter);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@PathVariable long id) {
        RecommendationRequestDto recommendationRequest = recommendationRequestService.getRequest(id);
        return ResponseEntity.ok(recommendationRequest);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(
            @PathVariable long id,
            @Valid @RequestBody RejectionDto rejection) {

        RecommendationRequestDto rejectRequest = recommendationRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok(rejectRequest);
    }
}
