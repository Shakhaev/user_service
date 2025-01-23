package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.faang.user_service.dto.event.RegisterParticipantRequest;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequestMapping("api/participation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public RegisterParticipantRequest registerParticipant(@RequestBody RegisterParticipantRequest registerParticipantRequest){
        return null;
    }
}
