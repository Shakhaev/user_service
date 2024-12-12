package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    public final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserService userService;
    private final List<MentorshipRequestFilter> filters;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toMentorshipRequest(mentorshipRequestDto);
        User requester = userService.getUserById(mentorshipRequestDto.requesterId());
        User receiver = userService.getUserById(mentorshipRequestDto.receiverId());

        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filterDto) {
        List<MentorshipRequest> mentorshipRequests = getFilteredMentorshipRequests(filterDto);
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequests);
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(long requestId) {
        MentorshipRequest mentorshipRequest = getMentorshipRequestById(requestId);
        mentorshipRequest.accept();
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long requestId, RejectionDto rejectionDto) {
        MentorshipRequest mentorshipRequest = getMentorshipRequestById(requestId);
        mentorshipRequest.reject();
        mentorshipRequest.setRejectionReason(rejectionDto.reason());
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }

    private List<MentorshipRequest> getFilteredMentorshipRequests(RequestFilterDto filterDto) {
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(
                        mentorshipRequestRepository.findAll(),
                        (mentorshipRequests, filter) -> filter.apply(mentorshipRequests, filterDto),
                        (list1, list2) -> list2
                );
    }

    private MentorshipRequest getMentorshipRequestById(long requestId) {
        return mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Mentorship request under id %d does not exist", requestId)));
    }
}
