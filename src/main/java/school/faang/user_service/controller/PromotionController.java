package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserPromotionRequest;

@RestController
@RequestMapping("/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/userprofile")
    public ResponseEntity<Void> buyUserPromotion(@Valid @RequestBody UserPromotionRequest request) {
        kafkaTemplate.send("test-topic", request.toString());
        return ResponseEntity.ok().build();
    }
}

