package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestValidator validator;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper requestMapper;
    private final List<MentorshipRequestFilter> filters;
    private final UserService userService;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validator.validate(mentorshipRequestDto);

        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();

        mentorshipRequestRepository.create(requesterId, receiverId, mentorshipRequestDto.getDescription());

        MentorshipRequest entity = getLatestRequest(requesterId, receiverId);

        return requestMapper.toDto(entity);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto incomeFilter) {
        List<MentorshipRequest> requests = mentorshipRequestRepository.findAll();
        List<MentorshipRequestFilter> applicableFilters =  filters.stream()
                .filter(filter -> filter.isApplicable(incomeFilter)).toList();
        if (applicableFilters.isEmpty()) {
            return List.of();
        }

        return applicableFilters.stream()
                .reduce(requests.stream(),
                        (requestStream, filter) -> filter.apply(requestStream, incomeFilter),
                        (list1, list2) -> list1)
                .map(requestMapper::toDto).toList();
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest request = getMentorshipRequestById(id);
        validator.validateRequesterHaveReceiverAsMentor(request);

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        requester.getMentors().add(receiver);
        receiver.getMentees().add(requester);
        request.setStatus(RequestStatus.ACCEPTED);

        userService.saveUser(requester);
        userService.saveUser(receiver);

        MentorshipRequest entity = mentorshipRequestRepository.save(request);

        return requestMapper.toDto(entity);
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        MentorshipRequest request = getMentorshipRequestById(id);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getReason());

        MentorshipRequest entity = mentorshipRequestRepository.save(request);

        return requestMapper.toDto(entity);
    }

    public MentorshipRequest getMentorshipRequestById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на менторство с id =" + id + " не найден"));
    }

    private MentorshipRequest getLatestRequest(long requesterId, long receiverId) {
        return mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на менторство с requesterId=" +
                        requesterId + "и receiverId=" + receiverId + " не найден"));
    }
}
