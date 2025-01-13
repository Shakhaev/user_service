package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.filter.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestServiceImpl service;

    @PostMapping
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("#requestMentorship: получен запрос на менторство от пользователя с id: {}", mentorshipRequestDto.getRequesterUserId());
        MentorshipRequestDto response = service.requestMentorship(mentorshipRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/requests")
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@RequestBody RequestFilterDto filter) {
        log.info("#getRequests: получен запрос на получение всех запросов на менторство, отвечающих фильтрам");
        List<MentorshipRequestDto> response = service.getRequests(filter);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/accept/{id}")
    public void acceptRequest(@PathVariable("id") long requestId) {
        log.info("#acceptRequest: получен запрос на принятие запроса на менторство от пользователя с id: {}", requestId);
        service.acceptRequest(requestId);
    }

    @PutMapping("/reject/{id}")
    public void rejectRequest(@PathVariable("id") long requestId, @RequestBody RejectionDto rejection) {
        log.info("#rejectRequest: получен запрос на отклонение запроса на менторство от пользователя с id: {}", requestId);
        service.rejectRequest(requestId, rejection);
    }
}
