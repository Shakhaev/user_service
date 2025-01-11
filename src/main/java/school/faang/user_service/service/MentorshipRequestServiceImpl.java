package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository repository;
    private final UserService userService;
    private final MentorshipRequestMapper mapper;
    private final List<RequestFilter> requestFilters;

    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateRequest(mentorshipRequestDto);
        MentorshipRequest mentorshipRequest = repository.create(
                mentorshipRequestDto.getRequesterUserId(),
                mentorshipRequestDto.getReceiverUserId(),
                mentorshipRequestDto.getDescription());
        return mapper.toMentorshipRequestDto(mentorshipRequest);
    }

    private void validateRequest(MentorshipRequestDto mentorshipRequestDto) {
        checkRequesterAndReceiverAreNotTheSamePerson(mentorshipRequestDto);
        UserDto requesterUserDto = userService.findById(mentorshipRequestDto.getRequesterUserId());
        UserDto receiverUserDto = userService.findById(mentorshipRequestDto.getReceiverUserId());
        MentorshipRequest lastMentorshipRequest = repository.findLatestRequest(
                requesterUserDto.getUserId(), receiverUserDto.getUserId()).orElse(null);
        if (!Objects.isNull(lastMentorshipRequest)) {
            checkRequestIsNotTooOften(lastMentorshipRequest);
        }
    }

    private void checkRequestIsNotTooOften(MentorshipRequest lastMentorshipRequest) {
        if (lastMentorshipRequest.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
            throw new IllegalArgumentException(
                    String.format("Запрос на менторство не может быть чаще чем раз в 3 месяца. " +
                            "Последний запрос был %s", lastMentorshipRequest.getCreatedAt()));
        }
    }

    private void checkRequesterAndReceiverAreNotTheSamePerson(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getRequesterUserId().equals(mentorshipRequestDto.getReceiverUserId())) {
            throw new IllegalArgumentException("Пользователь не может отправлять запрос сам себе!");
        }
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        Stream<MentorshipRequest> requestStream = repository.findAll().stream();
        return applyFilters(filters, requestStream);
    }

    private List<MentorshipRequestDto> applyFilters(RequestFilterDto filters, Stream<MentorshipRequest> requestStream) {
        for (RequestFilter filter : requestFilters) {
            if (filter.isApplicable(filters)) {
                filter.apply(requestStream, filters);
            }
        }
        return requestStream
                .map(mapper::toMentorshipRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void acceptRequest(long requestId) {
        MentorshipRequest request = repository.findById(requestId).orElseThrow(
                () -> new IllegalArgumentException(String.format("Запрос с id: %d отсутствует в базе данных", requestId)));

        User receiver = request.getReceiver();
        User requester = request.getRequester();

        User requestersMentorByReceiverId = requester.getMentors().stream()
                .filter(it -> receiver.getId().equals(it.getId()))
                .findAny()
                .orElse(null);
        if (Objects.isNull(requestersMentorByReceiverId)) {
            requester.getMentors().add(receiver);
            request.setStatus(RequestStatus.ACCEPTED);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Получатель запроса с id: %d уже является ментором отправителя", requestId));
        }
    }

    @Override
    public void rejectRequest(long requestId, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = repository.findById(requestId).orElseThrow(
                () -> new IllegalArgumentException(String.format("В базе данных отсутствует запрос с id: %d", requestId)));
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.reason());
    }
}
