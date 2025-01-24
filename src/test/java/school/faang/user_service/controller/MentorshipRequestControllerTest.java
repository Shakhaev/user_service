package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MentorshipRequestControllerTest {
    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void requestMentorship_ValidRequest_ReturnsOk() {
        MentorshipRequestDto requestDto = new MentorshipRequestDto();
        requestDto.setDescription("Нужна помощь с Java");
        when(mentorshipRequestService.requestMentorship(requestDto)).thenReturn(requestDto);

        ResponseEntity<MentorshipRequestDto> response =
                mentorshipRequestController.requestMentorship(requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requestDto, response.getBody());
        verify(mentorshipRequestService, times(1)).requestMentorship(requestDto);
    }

    @Test
    void getRequests_WithFilters_ReturnsFilteredRequests() {
        RequestFilterDto filters = new RequestFilterDto();
        filters.setDescription("Java");
        List<MentorshipRequestDto> expectedRequests =
                Collections.singletonList(new MentorshipRequestDto());
        when(mentorshipRequestService.getRequests(filters)).thenReturn(expectedRequests);

        ResponseEntity<List<MentorshipRequestDto>> response =
                mentorshipRequestController.getRequests(
                        "Java", null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRequests, response.getBody());
        verify(mentorshipRequestService, times(1)).getRequests(filters);
    }

    @Test
    void acceptRequest_ValidId_ReturnsOk() {
        long requestId = 1L;
        doNothing().when(mentorshipRequestService).acceptRequest(requestId);

        ResponseEntity<String> response = mentorshipRequestController.acceptRequest(requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Запрос на менторство успешно принят.", response.getBody());
        verify(mentorshipRequestService, times(1)).acceptRequest(requestId);
    }

    @Test
    void rejectRequest_ValidIdAndReason_ReturnsOk() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Не хватает опыта.");
        doNothing().when(mentorshipRequestService).rejectRequest(requestId, rejectionDto);

        ResponseEntity<String> response = mentorshipRequestController.rejectRequest(requestId, rejectionDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Запрос на менторство успешно отклонен.", response.getBody());
        verify(mentorshipRequestService, times(1)).rejectRequest(requestId, rejectionDto);
    }
}
