package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestMapper;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long idRequester = mentorshipRequestDto.requesterId();
        long idReceiver = mentorshipRequestDto.receiverId();
        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository.findLatestRequest(idRequester, idReceiver);

        if (userRepository.existsById(idRequester)
                && userRepository.existsById(idReceiver)
                && (idRequester != idReceiver)) {
            if (lastRequest.isPresent() && lastRequest.get().getCreatedAt()
                    .isBefore(LocalDateTime.now().minusMonths(3))) {
                mentorshipRequestRepository.save(mentorshipRequestMapper.toEntity(mentorshipRequestDto));
            }

        }
    }

    public List<MentorshipRequestDto> getRequest(RequestFilterDto filter) {
        List<MentorshipRequest> allMentorshipRequest = mentorshipRequestRepository.findAll();
        return allMentorshipRequest.stream()
                .filter(request ->
                        (filter.description() == null ||
                                request.getDescription().contains(filter.description())) &&
                                (filter.requesterId() == null ||
                                        request.getRequester().getId().equals(filter.requesterId())) &&
                                (filter.receiverId() == null ||
                                        request.getReceiver().getId().equals(filter.receiverId())) &&
                                (filter.status() == null ||
                                        request.getStatus().equals(filter.status())))
                .map(mentorshipRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public void acceptRequest(long id) {
        mentorshipRequestRepository.findById(id)
                .ifPresentOrElse(
                        mentorshipRequest -> {
                            User requester = mentorshipRequest.getRequester();
                            if (requester.getMentors().contains(mentorshipRequest.getReceiver())) {
                                log.error(mentorshipRequest.getReceiver().toString()
                                        + "is a mentor for this requester");
                                throw new IllegalArgumentException(
                                        String.format(mentorshipRequest.getReceiver().toString()
                                                + "is a mentor for this requester"));
                            } else {
                                requester.getMentors().add(mentorshipRequest.getReceiver());
                                mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
                            }
                        },
                        () ->
                        {
                            log.error("Event with id: %s does not exist" + id);
                            throw new IllegalArgumentException(String.format("Event with id: %s does not exist", id));
                        }
                );
    }

    public void rejectRequest(long id, String rejection) {
        mentorshipRequestRepository.findById(id)
                .ifPresentOrElse(
                        mentorshipRequest -> {
                            mentorshipRequest.setStatus(RequestStatus.REJECTED);
                            mentorshipRequest.setRejectionReason(rejection);
                        },
                        () ->
                        {
                            log.error("Event with id: %s does not exist");
                            throw new IllegalArgumentException(String.format("Event with id: %s does not exist", id));
                        }
                );
    }
}