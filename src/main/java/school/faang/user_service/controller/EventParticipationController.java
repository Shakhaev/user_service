package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    ResponseEntity<Object> register(@RequestParam long userId, @RequestParam long eventId) {
        return eventParticipationService.registerParticipant(eventId, userId);
    }

    @PostMapping("/unregister")
    ResponseEntity<Object> unregister(@RequestParam long userId, @RequestParam long eventId) {
        ResponseEntity<Object> response;
        try {
            eventParticipationService.unregister(eventId, userId);
            response = new ResponseEntity<>("success", HttpStatus.ACCEPTED);
        }
        catch (ResponseStatusException e) {
            response = new ResponseEntity<>(e.getReason(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            response = new ResponseEntity<>("something went wrong:(", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }


    @GetMapping("/participants/{eventId}")
    ResponseEntity<Object> getParticipants(@PathVariable long eventId) {
        ResponseEntity<Object> response;
        try {
            List<UserDto> listOfParticipants = eventParticipationService.getParticipant(eventId);
            response = new ResponseEntity<>(listOfParticipants, HttpStatus.ACCEPTED);
        }
        catch (Exception e) {
            response = new ResponseEntity<>("something went wrong:(", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/participants/count/{eventId}")
    ResponseEntity<Object> getParticipantsCount(@PathVariable long eventId) {
        ResponseEntity<Object> response;
        try {
            int count = eventParticipationService.getParticipantsCount(eventId);
            response = new ResponseEntity<>(count, HttpStatus.ACCEPTED);
        }
        catch (Exception e) {
            response = new ResponseEntity<>("something went wrong:(", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
