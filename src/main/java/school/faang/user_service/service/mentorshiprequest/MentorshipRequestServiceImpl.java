package school.faang.user_service.service.mentorshiprequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorshiprequest.MentorshipAcceptedEvent;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentroship.MentorshipStartEvent;
import school.faang.user_service.dto.rejection.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestedEvent;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.mentorshiprequest.MentorshipRequest;
import school.faang.user_service.entity.requeststatus.RequestStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.filters.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.publisher.mentorship.MentorshipStartEventPublisher;
import school.faang.user_service.publisher.mentorshiprequest.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.publisher.mentorship.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorshiprequest.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;
    private final MentorshipStartEventPublisher mentorshipStartEventPublisher;
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        mentorshipRequestValidator.validateRequesterAndReceiver(requesterId, receiverId);
        mentorshipRequestValidator.validateRequestInterval(requesterId, receiverId);

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(requesterId,
                receiverId, mentorshipRequestDto.getDescription());

        mentorshipRequestedEventPublisher.publish(new MentorshipRequestedEvent(
                mentorshipRequest.getId(),
                requesterId,
                receiverId,
                mentorshipRequest.getCreatedAt()
        ));
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        Stream<MentorshipRequest> mentorshipRequestStream = mentorshipRequestRepository.findAll().stream();

        for (var customFilter : mentorshipRequestFilters) {
            if (customFilter.isApplicable(requestFilterDto)) {
                mentorshipRequestStream = customFilter.apply(mentorshipRequestStream, requestFilterDto);
            }
        }

        return mentorshipRequestStream
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    @Override
    public MentorshipRequestDto acceptRequest(Long requestId) {
        MentorshipRequest mentorshipRequest = mentorshipRequestValidator.getRequestByIdOrThrowException(requestId);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        mentorshipRequestValidator.validateRequestStatus(mentorshipRequest, RequestStatus.ACCEPTED);
        if (mentorshipRequest.getStatus().equals(RequestStatus.REJECTED)) {
            mentorshipRequest.setRejectionReason(null);
        }

        requester.getMentors().add(receiver);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
        User mentor = mentorshipRepository.saveAndFlush(receiver);

        mentorshipStartEventPublisher.publish(new MentorshipStartEvent(
                mentor.getId(),
                requester.getId(),
                LocalDateTime.now()));

        mentorshipAcceptedEventPublisher.publish(new MentorshipAcceptedEvent(
                requestId,
                mentorshipRequest.getRequester().getId(),
                mentorshipRequest.getReceiver().getId(),
                LocalDateTime.now()
        ));

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Override
    public MentorshipRequestDto rejectRequest(Long requestId, RejectionDto rejectionDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestValidator.getRequestByIdOrThrowException(requestId);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        mentorshipRequestValidator.validateRequestStatus(mentorshipRequest, RequestStatus.REJECTED);
        if (mentorshipRequest.getStatus().equals(RequestStatus.ACCEPTED)) {
            requester.getMentors().remove(receiver);
        }

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectionDto.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }
}