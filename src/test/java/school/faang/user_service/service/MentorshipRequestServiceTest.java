package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.filter.mentorship_request.DescriptionFilter;
import school.faang.user_service.filter.mentorship_request.StatusFilter;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.PENDING;
import static school.faang.user_service.entity.RequestStatus.REJECTED;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {
    private static final long RECEIVER_ID = 2L;
    private static final long REQUESTER_ID = 1L;
    private static final long MENTORSHIP_REQUESTER_ID = 1L;
    private static final String DESCRIPTION = "описание";
    private static final String REJECTION_REASON = "описание отказа";

    @Mock
    private MentorshipRequestValidator validator;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Spy
    private MentorshipRequestMapperImpl requestMapper;

    @Mock
    private UserService userService;

    @Mock
    private DescriptionFilter descriptionFilter;

    @Mock
    private StatusFilter statusFilter;

    private MentorshipRequestService mentorshipRequestService;
    private RejectionDto rejectionDto;
    private User requester;
    private User receiver;
    private MentorshipRequest firstRequest;
    private MentorshipRequest secondRequest;
    private RequestFilterDto filterDto;

    @BeforeEach
    void setUp() {
        List<MentorshipRequestFilter> filters = List.of(descriptionFilter, statusFilter);
        mentorshipRequestService = new MentorshipRequestService(
                validator,
                mentorshipRequestRepository,
                requestMapper,
                filters,
                userService
        );
        requester = User.builder().id(REQUESTER_ID).mentors(new ArrayList<>()).build();
        receiver = User.builder().id(RECEIVER_ID).mentees(new ArrayList<>()).build();

        filterDto = RequestFilterDto.builder()
                .descriptionPattern(DESCRIPTION)
                .status(RequestStatus.ACCEPTED)
                .build();

        firstRequest = MentorshipRequest.builder()
                .id(1L)
                .status(RequestStatus.ACCEPTED)
                .description("большое описание1")
                .build();

        secondRequest = MentorshipRequest.builder()
                .id(2L)
                .status(PENDING)
                .description("это описание запроса2")
                .build();
    }

    @Test
    void testRequestMentorshipShouldSuccess() {
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description(DESCRIPTION)
                .build();
        MentorshipRequest entity = requestMapper.toEntity(dto);
        entity.setRequester(requester);
        entity.setReceiver(receiver);
        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong())).thenReturn(Optional.of(entity));

        MentorshipRequestDto expectedDto = requestMapper.toDto(entity);
        MentorshipRequestDto actualDto = mentorshipRequestService.requestMentorship(dto);
        verify(mentorshipRequestRepository).create(anyLong(), anyLong(), anyString());
        verify(mentorshipRequestRepository).findLatestRequest(anyLong(), anyLong());
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testRequestMentorshipShouldThrowBusinessException() {
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description(DESCRIPTION)
                .build();
        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.requestMentorship(dto));
    }

    @Test
    void testGetRequestsShouldReturnEmptyList() {
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of());

        List<MentorshipRequestDto> actualRequests = mentorshipRequestService.getRequests(filterDto);
        assertTrue(actualRequests.isEmpty());
    }

    @Test
    void testGetRequestsShouldReturnFilteredList() {
        List<MentorshipRequest> elements = List.of(firstRequest, secondRequest);
        when(mentorshipRequestRepository.findAll()).thenReturn(elements);
        when(descriptionFilter.isApplicable(any(RequestFilterDto.class))).thenReturn(true);
        when(descriptionFilter.apply(any(), any(RequestFilterDto.class))).thenReturn(Stream.of(firstRequest, secondRequest));
        when(statusFilter.isApplicable(any(RequestFilterDto.class))).thenReturn(true);
        when(statusFilter.apply(any(), any(RequestFilterDto.class))).thenReturn(Stream.of(firstRequest));

        List<MentorshipRequestDto> actualRequests = mentorshipRequestService.getRequests(filterDto);
        MentorshipRequestDto expectedRequestDto = requestMapper.toDto(firstRequest);
        assertTrue(actualRequests.contains(expectedRequestDto));
    }

    @Test
    void testAcceptRequestShouldSuccess() {
        MentorshipRequest entity = MentorshipRequest.builder()
                .id(MENTORSHIP_REQUESTER_ID)
                .requester(requester)
                .description(DESCRIPTION)
                .receiver(receiver)
                .build();

        when(mentorshipRequestRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(mentorshipRequestRepository.save(entity)).thenReturn(entity);

        MentorshipRequestDto actualDto = mentorshipRequestService.acceptRequest(entity.getId());
        verify(mentorshipRequestRepository).save(entity);
        verify(mentorshipRequestRepository).findById(entity.getId());
        verify(userService).saveUser(receiver);
        verify(userService).saveUser(requester);

        assertEquals(receiver.getId(), actualDto.getReceiverId());
        assertEquals(requester.getId(), actualDto.getRequesterId());
        assertEquals(entity.getId(), actualDto.getId());
        assertEquals(entity.getDescription(), actualDto.getDescription());
    }

    @Test
    void testAcceptRequestShouldThrowEntityNotFoundExceptionIfRequestNotExists() {
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.acceptRequest(anyLong()));
        verify(mentorshipRequestRepository, times(0)).save(any(MentorshipRequest.class));
        verify(userService, times(0)).saveUser(any(User.class));
    }

    @Test
    void testAcceptRequestShouldThrowBusinessExceptionExceptionIfReceiverAlreadyIsMentor() {
        requester.getMentors().add(receiver);
        MentorshipRequest entity = MentorshipRequest.builder()
                .id(MENTORSHIP_REQUESTER_ID)
                .requester(requester)
                .description(DESCRIPTION)
                .receiver(receiver)
                .build();
        doThrow(BusinessException.class).when(validator).validateRequesterHaveReceiverAsMentor(any());
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        assertThrows(BusinessException.class,
                () -> mentorshipRequestService.acceptRequest(anyLong()));
        verify(mentorshipRequestRepository, times(0)).save(any(MentorshipRequest.class));
        verify(userService, times(0)).saveUser(any(User.class));
    }

    @Test
    void testRejectRequestShouldSuccess() {
        rejectionDto = new RejectionDto(REJECTION_REASON);
        MentorshipRequest entity = MentorshipRequest.builder()
                .id(MENTORSHIP_REQUESTER_ID)
                .requester(requester)
                .description(DESCRIPTION)
                .receiver(receiver)
                .build();
        MentorshipRequestDto expectedDto = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description(DESCRIPTION)
                .status(REJECTED)
                .id(entity.getId())
                .build();
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(mentorshipRequestRepository.save(entity)).thenReturn(entity);
        MentorshipRequestDto actualDto = mentorshipRequestService.rejectRequest(entity.getId(), rejectionDto);
        assertEquals(expectedDto.getId(), actualDto.getId());
        assertEquals(expectedDto.getRequesterId(), actualDto.getRequesterId());
        assertEquals(expectedDto.getReceiverId(), actualDto.getReceiverId());
        assertEquals(expectedDto.getStatus(), actualDto.getStatus());
        assertEquals(expectedDto.getDescription(), actualDto.getDescription());
    }

    @Test
    void testRejectRequestShouldShouldThrowEntityNotFoundExceptionIfRequestNotExists() {
        rejectionDto = new RejectionDto(REJECTION_REASON);
        when(mentorshipRequestRepository.findById(MENTORSHIP_REQUESTER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.rejectRequest(MENTORSHIP_REQUESTER_ID, rejectionDto));
        verify(mentorshipRequestRepository, times(0)).save(any(MentorshipRequest.class));
    }
}