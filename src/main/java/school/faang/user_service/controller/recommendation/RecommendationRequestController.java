package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestResponseDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public ResponseEntity<RecommendationRequestResponseDto> requestRecommendation(@Valid @RequestBody RecommendationRequestCreateDto recommendationRequest) {
        var recommendationRequestDto = recommendationRequestService.create(recommendationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(recommendationRequestDto);
    }

    @GetMapping
    public List<RecommendationRequestResponseDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestResponseDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping( "/{id}")
    public ResponseEntity<Void> rejectRequest(@PathVariable long id, RejectionDto rejection) {
        recommendationRequestService.rejectRequest(id, rejection);
        return ResponseEntity.noContent().build();
    }
}
