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
    private final MentorshipRequestRepository requestRepository;
    private final MentorshipRequestValidator requestValidator;
    private final UserRepository userRepository;
    private final List<RequestFilter> requestFilters;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        requestValidator.validateRequestForMentorship(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId());

        User requester = userRepository.findById(mentorshipRequestDto.getRequesterId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User receiver = userRepository.findById(mentorshipRequestDto.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        MentorshipRequest requestEntity = requestMapper.toEntity(mentorshipRequestDto);
        requestEntity.setRequester(requester);
        requestEntity.setReceiver(receiver);

        requester.getSentMentorshipRequests().add(requestEntity);

        userRepository.save(requester);
        requestRepository.save(requestEntity);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filtersRequested) {
        Stream<MentorshipRequest> mentorshipRequests = requestRepository.findAll().stream();
        return requestFilters.stream()
                .filter(filter -> filter.isApplicable(filtersRequested))
                .flatMap(filter -> filter.apply(mentorshipRequests, filtersRequested))
                .map(requestMapper::toDto)
                .toList();
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
        request.setCreatedAt(LocalDateTime.now());

        return requestMapper.toDto(requestRepository.save(request));
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = requestValidator.validateRequestId(id);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getRejectionReason());

        requestMapper.toRejectionDto(requestRepository.save(request));
    }
}
