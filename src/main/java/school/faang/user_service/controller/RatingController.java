package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.rating.RatingDTO;
import school.faang.user_service.service.RatingService;

@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    /*
        For kafka integration
     */
    @PostMapping
    public void addRatingToUser(@RequestBody RatingDTO ratingDTO) {
        ratingService.addPoints(ratingDTO);
    }
}
