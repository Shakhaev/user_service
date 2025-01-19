package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.filter.RequestFilter;
import school.faang.user_service.service.filter.RequestFilterDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private static final int MIN_COUNT_OF_MONTHS_BETWEEN_REQUESTS = 3;
    private final MentorshipRequestRepository repository;
    private final UserService userService;
    private final MentorshipRequestMapper mapper;
    private final List<RequestFilter> requestFilters;

    @Override
    public MentorshipResponseDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        log.info("MentorshipRequestServiceImpl: method #requestMentorship started with data: {}", mentorshipRequestDto);
        validateRequest(mentorshipRequestDto);
        MentorshipRequest mentorshipRequest = repository.create(
                mentorshipRequestDto.requesterUserId(),
                mentorshipRequestDto.receiverUserId(),
                mentorshipRequestDto.description());
        return mapper.toMentorshipResponseDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipResponseDto> getRequests(RequestFilterDto filters) {
        log.info("MentorshipRequestServiceImpl: method #getRequests started with filters: {}", filters);
        Stream<MentorshipRequest> mentorshipRequests = repository.findAll().stream();

        for (RequestFilter requestFilter : requestFilters) {
            mentorshipRequests = requestFilter.apply(mentorshipRequests, filters)
                    .toList().stream();
        }

        return mentorshipRequests
                .map(mapper::toMentorshipResponseDto)
                .toList();
    }

    @Override
    public void acceptRequest(long requestId) {
        log.info("MentorshipRequestServiceImpl: method #acceptRequest started with requestId: {}", requestId);
        MentorshipRequest request = repository.findById(requestId).orElseThrow(
                () -> new IllegalArgumentException(String.format("Запрос с id: %d отсутствует в базе данных", requestId)));

        User receiver = request.getReceiver();
        User requester = request.getRequester();

        requester.getMentors().stream()
                .filter(it -> receiver.getId().equals(it.getId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Получатель запроса с id: %d уже является ментором отправителя", requestId)));

        requester.getMentors().add(receiver);
        request.setStatus(RequestStatus.ACCEPTED);
    }

    @Override
    public void rejectRequest(long requestId, RejectionDto rejection) {
        log.info("MentorshipRequestServiceImpl: method #rejectRequest started with requestId: {} and data: {}", requestId, rejection);
        MentorshipRequest mentorshipRequest = repository.findById(requestId).orElseThrow(
                () -> new IllegalArgumentException(String.format("В базе данных отсутствует запрос с id: %d", requestId)));
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.reason());
    }

    private void validateRequest(MentorshipRequestDto mentorshipRequestDto) {
        checkRequesterAndReceiverAreNotTheSamePerson(mentorshipRequestDto);
        UserDto requesterUserDto = userService.findById(mentorshipRequestDto.requesterUserId());
        UserDto receiverUserDto = userService.findById(mentorshipRequestDto.receiverUserId());
        MentorshipRequest lastMentorshipRequest = repository.findLatestRequest(
                requesterUserDto.getUserId(), receiverUserDto.getUserId()).orElse(null);
        if (!Objects.isNull(lastMentorshipRequest)) {
            checkRequestIsNotTooOften(lastMentorshipRequest);
        }
    }

    private void checkRequesterAndReceiverAreNotTheSamePerson(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.requesterUserId().equals(mentorshipRequestDto.receiverUserId())) {
            throw new IllegalArgumentException("Пользователь не может отправлять запрос сам себе!");
        }
    }


    private void checkRequestIsNotTooOften(MentorshipRequest lastMentorshipRequest) {
        if (lastMentorshipRequest.getCreatedAt().isAfter(
                LocalDateTime.now().minusMonths(MIN_COUNT_OF_MONTHS_BETWEEN_REQUESTS))) {
            throw new IllegalArgumentException(
                    String.format("Запрос на менторство не может быть чаще чем раз в 3 месяца. " +
                            "Последний запрос был %s", lastMentorshipRequest.getCreatedAt()));
        }
    }
}
