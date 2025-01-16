package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventPromotionRequest;
import school.faang.user_service.dto.UserPromotionRequest;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.UserService;

@RestController
@RequestMapping("/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final UserService userService;
    private final EventService eventService;

    @PostMapping("/userprofile")
    public ResponseEntity<Void> buyUserPromotion(@Valid @RequestBody UserPromotionRequest request) {
        userService.userPromotion(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/event")
    public ResponseEntity<Void> buyEvent(@Valid @RequestBody EventPromotionRequest request) {
        eventService.eventPromotion(request);
        return ResponseEntity.ok().build();
    }
}

