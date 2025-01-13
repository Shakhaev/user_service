package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filters.RequestFilter;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestMapper requestMapper;
    private final MentorshipRequestRepository requestRepository;
    private final MentorshipRequestValidator requestValidator;
    private final UserRepository userRepository;
    private final List<RequestFilter> requestFilters;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();
        requestValidator.validateUsersIdNotEqual(requesterId, receiverId);
        requestValidator.validateUserExists(requesterId);
        requestValidator.validateUserExists(receiverId);
        requestValidator.validateLastRequestData(requesterId, receiverId);

        MentorshipRequest request = requestMapper.toEntity(mentorshipRequestDto);
        User requester = userRepository.findById(requesterId).get();
        User receiver = userRepository.findById(receiverId).get();
        request.setRequester(requester);
        request.setReceiver(receiver);
        requester.getSentMentorshipRequests().add(request);
        userRepository.save(requester);
        requestRepository.save(request);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filterRequested) {
        Stream<MentorshipRequest> mentorshipRequests = requestRepository.findAll().stream();
        requestFilters.stream()
                .filter(filter -> filter.isApplicable(filterRequested))
                .forEach(filter -> filter.apply(mentorshipRequests, filterRequested));
        return requestMapper.toRequestsListDto(mentorshipRequests.toList());
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest request = requestValidator.validateRequestId(id);
        User requester = request.getRequester();
        User receiver = request.getReceiver();
        requestValidator.validateNotMentorYet(requester, receiver);
        requester.getMentors().add(receiver);
        userRepository.save(requester);
        receiver.getMentees().add(requester);
        userRepository.save(receiver);
        request.setStatus(RequestStatus.ACCEPTED);
        return requestMapper.toDto(requestRepository.save(request));
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = requestValidator.validateRequestId(id);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getRejectionReason());
        requestMapper.toRejectionDto(requestRepository.save(request));
    }
}
