package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.events.MentorshipStartEvent;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.AbstractEventPublisher;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestValidator validator;
    private final UserService userService;
    private final UserContext userContext;
    private final MentorshipStartEventPublisher mentorshipStartEventPublisher;

    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto requestFilterDto) {
        List<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll();

        return mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .reduce(mentorshipRequests.stream(),
                        (stream, filter) -> filter.apply(stream, requestFilterDto),
                        (s1, s2) -> s1)
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    public MentorshipRequestDto createRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        long requesterUserId = mentorshipRequestDto.getRequesterUserId();
        long receiverUserId = mentorshipRequestDto.getReceiverUserId();

        LocalDateTime latestRequestCreatedAt = getLatestRequest(requesterUserId, receiverUserId)
                .map(MentorshipRequest::getCreatedAt)
                .orElse(null);


        validator.validate(
                mentorshipRequestDto,
                userService.existsById(requesterUserId),
                userService.existsById(receiverUserId),
                latestRequestCreatedAt
        );

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequest.setRequester(userService.getUserById(mentorshipRequestDto.getRequesterUserId()));
        mentorshipRequest.setReceiver(userService.getUserById(mentorshipRequestDto.getReceiverUserId()));
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setCreatedAt(LocalDateTime.now());
        mentorshipRequest.setUpdatedAt(LocalDateTime.now());

        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(mentorshipRequest));
    }

    public MentorshipRequestDto acceptRequest(long id) {
        log.info("Getting mentorshipRequest entity from db");
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(id);

        User requesterUser = mentorshipRequest.getRequester();
        User receiverUser = mentorshipRequest.getReceiver();

        log.info("validate that mentor does not contains current mentee");
        if (requesterUser.getMentors().contains(receiverUser)) {
            throw new IllegalStateException("Mentorship request already accepted");
        }

        requesterUser.getMentors().add(receiverUser);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        log.info("publish mentorshipStartEvent event to redis");
        MentorshipStartEvent mentorshipStartEvent = new MentorshipStartEvent(receiverUser.getId(), requesterUser.getId(), getContextId());
        mentorshipStartEventPublisher.publish(mentorshipStartEvent);

        log.info("update mentorshipRequest and map it to dto");
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(mentorshipRequest));
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(id);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());

        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(mentorshipRequest));
    }

    public Optional<MentorshipRequest> getLatestRequest(long requesterId, long receiverId) {
        return mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);
    }

    private MentorshipRequest findMentorshipRequestById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("mentorship request not found"));
    }

    private long getContextId() {
        return userContext.getUserId();
    }
}
