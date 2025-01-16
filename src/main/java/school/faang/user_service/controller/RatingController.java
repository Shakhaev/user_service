package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.rating.LeaderTableDto;
import school.faang.user_service.dto.rating.UserComparingDto;
import school.faang.user_service.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping("/leaders/{limit}")
    public List<LeaderTableDto> getTableLeaders(@PathVariable int limit, @RequestBody UserComparingDto userComparingDto) {
        return ratingService.getTableLeaders(limit, userComparingDto);
    }

    @GetMapping("/leaders/last12hours")
    public List<LeaderTableDto> getTableLeadersLast12Hours() {
        return ratingService.getTableLeadersLast12Hours();
    }
}
