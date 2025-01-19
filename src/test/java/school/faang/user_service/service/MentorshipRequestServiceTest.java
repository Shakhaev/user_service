package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.AuthorFilter;
import school.faang.user_service.service.filter.DescriptionFilter;
import school.faang.user_service.service.filter.ReceiverFilter;
import school.faang.user_service.service.filter.RequestFilter;
import school.faang.user_service.service.filter.RequestFilterDto;
import school.faang.user_service.service.filter.StatusFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MentorshipRequestServiceTest {
    MentorshipRequestRepository mentorshipRequestRepositoryMock;
    UserService userServiceMock;
    MentorshipRequestMapper mapperSpy;
    AuthorFilter authorFilterMock;
    DescriptionFilter descriptionFilterMock;
    ReceiverFilter receiverFilterMock;
    StatusFilter statusFilterMock;
    List<RequestFilter> filters;
    MentorshipRequestService mentorshipRequestService;


    @BeforeEach
    void init() {
        mentorshipRequestRepositoryMock = Mockito.mock(MentorshipRequestRepository.class);
        userServiceMock = Mockito.mock(UserService.class);
        mapperSpy = Mockito.spy(MentorshipRequestMapperImpl.class);

        authorFilterMock = Mockito.spy(AuthorFilter.class);
        descriptionFilterMock = Mockito.spy(DescriptionFilter.class);
        receiverFilterMock = Mockito.spy(ReceiverFilter.class);
        statusFilterMock = Mockito.spy(StatusFilter.class);

        filters = List.of(authorFilterMock, descriptionFilterMock, receiverFilterMock, statusFilterMock);

        mentorshipRequestService =
                new MentorshipRequestServiceImpl(mentorshipRequestRepositoryMock, userServiceMock, mapperSpy, filters);
    }

    @Test
    public void testRequestMentorshipWithoutDescriptionFailed() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(1L)
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithTheSameRequesterAndReceiverIdFailed() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(1L)
                .description("some description")
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithNonExistentRequesterUserIdFailed() {
        Mockito.when(userServiceMock.findById(1L)).thenThrow(new IllegalArgumentException());

        var requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(2L)
                .description("some description")
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithNonExistentReceiverUserIdFailed() {
        Mockito.when(userServiceMock.findById(1L)).thenReturn(UserDto.builder().userId(1L).build());
        Mockito.when(userServiceMock.findById(2L)).thenThrow(new IllegalArgumentException());

        var requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(2L)
                .description("some description")
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithTooFrequentRequestFailed() {
        long requesterId = 1L;
        long receiverId = 2L;
        MentorshipRequest latestMentorshipRequest = new MentorshipRequest();
        latestMentorshipRequest.setId(requesterId);
        latestMentorshipRequest.setCreatedAt(LocalDateTime.now().minusDays(89));
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(2L)
                .description("some description")
                .build();
        UserDto requester = UserDto.builder()
                .userId(1L)
                .build();
        UserDto receiver = UserDto.builder()
                .userId(2L)
                .build();
        Mockito.when(mentorshipRequestRepositoryMock.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(latestMentorshipRequest));
        Mockito.when(userServiceMock.findById(1L)).thenReturn(requester);
        Mockito.when(userServiceMock.findById(2L)).thenReturn(receiver);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipSuccess() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(2L)
                .description("description")
                .build();

        Mockito.when(userServiceMock.findById(1L)).thenReturn(UserDto.builder().userId(1L).build());
        Mockito.when(userServiceMock.findById(2L)).thenReturn(UserDto.builder().userId(2L).build());

        mentorshipRequestService.requestMentorship(requestDto);
        Mockito.verify(mentorshipRequestRepositoryMock, Mockito.times(1))
                .create(1L, 2L, "description");
    }

    @Test
    public void testGetRequestsSuccess() {
        RequestFilterDto filters = RequestFilterDto.builder()
                .authorPattern("Ali")
                .statusPattern("accept")
                .descriptionPattern("desc")
                .receiverPattern("Jack")
                .build();
        Mockito.when(mentorshipRequestRepositoryMock.findAll())
                .thenReturn(TestData.getListOfRequests());

        List<MentorshipResponseDto> requests = mentorshipRequestService.getRequests(filters);

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(5L, (long) requests.get(0).id());
    }
}
