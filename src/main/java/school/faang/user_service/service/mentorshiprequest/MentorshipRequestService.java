package school.faang.user_service.service.mentorshiprequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshiprequest.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.dto.message.MentorshipRequestMessage;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorshiprequest.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipRequestEventPublisher;
import school.faang.user_service.publisher.mentorshipStart.MentorshipStartEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorshiprequst.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@ToString
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository menReqRepository;
    private final MentorshipRequestMapper menReqMapper;
    private final MentorshipRequestValidator menReqValidator;
    private final List<MentorshipRequestFilter> filtersRequests;
    private final MentorshipRequestEventPublisher mentorshipRequestEventPublisher;
    private final MentorshipStartEventPublisher mentorshipStartEventPublisher;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto menReqDto) {
        menReqValidator.validateReceiverNoEqualsRequester(menReqDto);
        menReqValidator.validateAvailabilityUsersDB(menReqDto);

        Optional<MentorshipRequest> menReqOptional =
                menReqRepository.findLatestRequest(menReqDto.getRequesterId(), menReqDto.getReceiverId());

        menReqOptional.ifPresent(menReqValidator::validateDataCreateRequest);

        Long menReqId = menReqRepository.create(menReqDto.getRequesterId(),
                menReqDto.getReceiverId(),
                menReqDto.getDescription());

        MentorshipRequest menReq = menReqRepository.findById(menReqId)
                .orElseThrow(() -> new NoSuchElementException("MentorshipRequest was not found"));

        publishMentorshipStartEvent(menReqDto.getRequesterId(), menReqDto.getReceiverId());
        publishMentorshipRequestMessage(menReqDto.getRequesterId(), menReqDto.getReceiverId());

        return menReqMapper.toDto(menReq);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        Stream<MentorshipRequest> requests = menReqRepository.findAll().stream();

        return filtersRequests.stream()
                .filter(filterRequest -> filterRequest.isApplicable(filter))
                .flatMap(filterRequest -> filterRequest.apply(requests, filter))
                .map(menReqMapper::toDto)
                .toList();
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest menReq = menReqRepository.findById(id).map(mentorshipRequest -> {

            menReqValidator.validateMentorsContainsReceiver(mentorshipRequest);

            mentorshipRequest.getReceiver().getMentors().add(mentorshipRequest.getRequester());
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

            return mentorshipRequest;
        }).orElseThrow(() -> {

            entityNotFoundException(id);
            return null;
        });

        return menReqMapper.toDto(menReqRepository.save(menReq));
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest menReq = menReqRepository.findById(id).map(mentorshipRequest -> {

            mentorshipRequest.setStatus(RequestStatus.REJECTED);
            mentorshipRequest.setRejectionReason(rejection.getReason());


            return mentorshipRequest;
        }).orElseThrow(() -> {
            entityNotFoundException(id);
            return null;
        });

        return menReqMapper.toDto(menReqRepository.save(menReq));
    }

    private void entityNotFoundException(long id) {
        throw new EntityNotFoundException("No such request was found " + id);
    }

    private void publishMentorshipStartEvent(Long mentorId, Long menteeId) {
        MentorshipStartEvent mentorshipStartEvent = MentorshipStartEvent.builder()
                .mentorId(mentorId)
                .menteeId(menteeId)
                .build();
        try {
            mentorshipStartEventPublisher.publish(mentorshipStartEvent);
        } catch (Exception ex) {
            log.error("Failed to send notification: {}", mentorshipStartEvent.toString(), ex);
        }
    }

    private void publishMentorshipRequestMessage(Long requesterId, Long receiverId) {
        var message = MentorshipRequestMessage.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .createdAt(LocalDateTime.now())
                .build();

        mentorshipRequestEventPublisher.publish(message);
        log.info("message publish: {}", message.toString());
    }
}
