package school.faang.user_service.controller.recommendation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestRcvDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestServiceImpl;

import java.util.List;

import static school.faang.user_service.utils.Constants.API_VERSION_1;

@RestController
@AllArgsConstructor
@RequestMapping(API_VERSION_1 + "/recommendation-request")
public class RecommendationRequestController {
    private final RecommendationRequestServiceImpl recommendationRequestService;

    @PostMapping("/create")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestRcvDto requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("requestDto cannot be null");
        }
        if (StringUtils.isBlank(requestDto.message())) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        return recommendationRequestService.createRequest(requestDto);
    }

    @PostMapping("/getbyfilters")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filters) {
        if (filters == null) {
            throw new IllegalArgumentException("filters cannot be null");
        }
        return recommendationRequestService.getRequests(filters);
    }

    @GetMapping("/get/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping("/reject/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejectionDto) {
        if (rejectionDto == null) {
            throw new IllegalArgumentException("rejectionDto cannot be null");
        }
        if (StringUtils.isBlank(rejectionDto.reason())) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
        return recommendationRequestService.rejectRequest(id, rejectionDto);
    }
}
