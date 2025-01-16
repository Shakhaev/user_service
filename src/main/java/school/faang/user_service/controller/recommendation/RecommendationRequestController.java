package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.*;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/recommendations/request")
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/create")
    public ResponseEntity<CreateRecommendationRequestResponse> requestRecommendation(@Valid @RequestBody CreateRecommendationRequestRequest recommendationRequest) {
        var recommendationRequestDto = recommendationRequestService.create(recommendationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(recommendationRequestDto);
    }

    @GetMapping
    public List<GetRecommendationRequestResponse> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public GetRecommendationRequestResponse getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping( "/{id}")
    public ResponseEntity<Void> rejectRequest(@PathVariable long id, RejectionDto rejection) {
        recommendationRequestService.rejectRequest(id, rejection);
        return ResponseEntity.noContent().build();
    }
}
