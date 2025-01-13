package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestSaveDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping(path = "/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(@Valid @RequestBody RecommendationRequestSaveDto recommendationRequest) {
        var recommendationRequestDto = recommendationRequestService.create(recommendationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(recommendationRequestDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> rejectRequest(@PathVariable long id, RejectionDto rejection) {
        recommendationRequestService.rejectRequest(id, rejection);
        return ResponseEntity.noContent().build();
    }
}
