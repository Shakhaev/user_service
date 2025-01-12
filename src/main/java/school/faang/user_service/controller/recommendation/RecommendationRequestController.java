package school.faang.user_service.controller.recommendation;

import com.amazonaws.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    @PostMapping(path = "/request-recommendation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        try {
            if (recommendationRequest == null || StringUtils.isNullOrEmpty(recommendationRequest.getMessage())) {
                throw new IllegalArgumentException("Message must not be null");
            }
            return recommendationRequestMapper.toDto(recommendationRequestService.create(recommendationRequest));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/filter")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        return recommendationRequestService.getRequestByFilter(filter).stream()
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        try {
            return recommendationRequestMapper.toDto(recommendationRequestService.getRequestById(id));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
