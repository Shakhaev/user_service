package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequestMapping("/api/v1/recommendations")
@Validated
@AllArgsConstructor
public class RecommendationController {

    private RecommendationService service;

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
    public ResponseEntity<Void> deleteRecommendation(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/receiver/{receiverId}/{page}/{size}")
    public Page<RecommendationDto> getAllUserRecommendations(
            @PathVariable long receiverId,
            @PathVariable int page,
            @PathVariable int size) {

        Page<RecommendationDto> recommendations = service.getAllUserRecommendations(receiverId, page, size);

        if (recommendations == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No recommendations found for receiverId: " + receiverId);
        }
        return recommendations;
    }

    @GetMapping("/author/{authorId}/{page}/{size}")
    public Page<RecommendationDto> getAllGivenRecommendations(
            @PathVariable long authorId,
            @PathVariable int page,
            @PathVariable int size) {

        Page<RecommendationDto> recommendations = service.getAllGivenRecommendations(authorId, page, size);

        if (recommendations == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No recommendations found for authorId: " + authorId);
        }
        return recommendations;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        throw new DataValidationException(errorMessage);
    }
}