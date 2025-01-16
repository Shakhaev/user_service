package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.RequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final static String SELF_REQUEST = "Requester and receiver are the same";
    private final static String USER_NOT_EXIST = "User does not exist in the database";
    private final static String REQUEST_NOT_EXIST = "Request does not exist in the database";
    private final static String ALREADY_MENTOR = "The receiver is already a mentor for the requester";
    private final static String TOO_EARLY = "А request can be made at three months";


    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;
    private final List<RequestFilter> requestFilters;


    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        if (requesterId.equals(receiverId)) {
            throw new RuntimeException(SELF_REQUEST);
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequest.setRequester(userRepository
                .findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_EXIST))
        );
        mentorshipRequest.setReceiver(userRepository
                .findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_EXIST))
        );

        mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .map(request -> request.getCreatedAt().plusMonths(3))
                .filter(creationDate -> LocalDateTime.now().isBefore(creationDate))
                .ifPresent(creationDate -> {
                    throw new RuntimeException(TOO_EARLY);
                });

        return mentorshipRequestMapper.toDto(mentorshipRequestRepository
                .create(
                        requesterId,
                        receiverId,
                        mentorshipRequestDto.getDescription()
                )
        );
    }

    @Transactional
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll().stream();
        return mentorshipRequestMapper.toDto(
                requestFilters
                        .stream()
                        .filter(filter -> filter.isApplicable(filters))
                        .reduce(mentorshipRequests,
                                (stream, filter) -> filter.apply(stream, filters),
                                (s1, s2) -> s1)
                        .toList()
        );
    }

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_EXIST));

        mentorshipRequest.getRequester().getMentors().stream()
                .filter(user -> user.equals(mentorshipRequest.getReceiver())).findFirst()
                .ifPresent(user -> {
                    throw new RuntimeException(ALREADY_MENTOR);
                });

        mentorshipRequest.getRequester().getMentors().add(mentorshipRequest.getReceiver());
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_EXIST));

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
    }
}
