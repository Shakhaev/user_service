package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestResponseDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    @PostMapping
    public ResponseEntity<RecommendationRequestResponseDto> requestRecommendation(
            @RequestBody RecommendationRequestDto requestDto) {
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(requestDto);
        RecommendationRequest createdRequest = recommendationRequestService.create(recommendationRequest);
        RecommendationRequestResponseDto responseDto = recommendationRequestMapper.toResponseDto(createdRequest);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<RecommendationRequestResponseDto>> getRecommendationRequests(
            @RequestBody RequestFilterDto filter) {
        List<RecommendationRequest> requests = recommendationRequestService.getRequests(filter);
        List<RecommendationRequestResponseDto> response = recommendationRequestMapper.toResponseDtoList(requests);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestResponseDto> getRecommendationRequest(@PathVariable long id) {
        RecommendationRequest request = recommendationRequestService.getRequest(id);
        RecommendationRequestResponseDto responseDto = recommendationRequestMapper.toResponseDto(request);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<RecommendationRequestResponseDto> rejectRequest(
            @PathVariable long id, @RequestBody RejectionDto rejectionDto) {
        RecommendationRequest rejectedRequest = recommendationRequestService.rejectRequest(id, rejectionDto.getReason());
        RecommendationRequestResponseDto responseDto = recommendationRequestMapper.toResponseDto(rejectedRequest);
        return ResponseEntity.ok(responseDto);
    }
}
