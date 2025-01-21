package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRejectionMapper;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filters.RequestFilter;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestMapper requestMapper;
    private final MentorshipRejectionMapper rejectionMapper;
    private final MentorshipRequestRepository requestRepository;
    private final MentorshipRequestValidator requestValidator;
    private final UserRepository userRepository;
    private final List<RequestFilter> requestFilters;

    public MentorshipRequest requestMentorship(MentorshipRequest requestEntity) {
        long requesterId = requestEntity.getRequester().getId();
        long receiverId = requestEntity.getRequester().getId();

        if (!requestValidator.validateLastRequestData(requesterId, receiverId)) {
            throw new DataValidationException("Too early for next mentorship request");
        } else {
            User requester = userRepository.findById(requesterId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            requestEntity.setRequester(requester);
            requestEntity.setReceiver(receiver);

            requester.getSentMentorshipRequests().add(requestEntity);

            userRepository.save(requester);
            return requestRepository.save(requestEntity);
        }
    }

    public List<MentorshipRequest> getRequests(RequestFilterDto filtersRequested) {
        Stream<MentorshipRequest> mentorshipRequests = requestRepository.findAll().stream();
        return requestFilters.stream()
                .filter(filter -> filter.isApplicable(filtersRequested))
                .flatMap(filter -> filter.apply(mentorshipRequests, filtersRequested))
                .toList();
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        User requester = request.getRequester();
        User receiver = request.getReceiver();
        requestValidator.validateNotMentorYet(requester, receiver);

        requester.getMentors().add(receiver);
        userRepository.save(requester);

        receiver.getMentees().add(requester);
        userRepository.save(receiver);

        request.setStatus(RequestStatus.ACCEPTED);
        request.setCreatedAt(LocalDateTime.now());

        return requestMapper.toDto(requestRepository.save(request));
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getRejectionReason());
    }
}
