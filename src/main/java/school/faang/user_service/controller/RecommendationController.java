package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequestMapping("/api/v1/recommendations")
@Validated
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService service;

    @PostMapping
    public ResponseEntity<RecommendationDto> giveRecommendation(
            @Valid @RequestBody RecommendationDto recommendation) {
        RecommendationDto retRecommendation = service.create(recommendation);
        return ResponseEntity.ok(retRecommendation);
    }

    @PutMapping
    public ResponseEntity<RecommendationDto> updateRecommendation(
            @Valid @RequestBody RecommendationDto recommendation) {
        RecommendationDto retRecommendation = service.update(recommendation);
        return ResponseEntity.ok(retRecommendation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(
            @PathVariable
            @Min(value = 1, message = "The recommendation ID cannot be less than or equal to zero!") long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/receiver/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(
            @PathVariable long receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<RecommendationDto> recommendations = service.getAllUserRecommendations(receiverId, page, size);

        if (recommendations == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No recommendations found for receiverId: " + receiverId);
        }
        return recommendations;
    }

    @GetMapping("/author/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(
            @PathVariable long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<RecommendationDto> recommendations = service.getAllGivenRecommendations(authorId, page, size);

        if (recommendations == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No recommendations found for authorId: " + authorId);
        }
        return recommendations;
    }
}