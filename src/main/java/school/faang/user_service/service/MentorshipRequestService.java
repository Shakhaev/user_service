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
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

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

        mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());
        log.info("Запрос на менторство {} создан", mentorshipRequestDto);

        return mentorshipRequestDto;
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto incomeFilter) {
        Stream<MentorshipRequest> requests = mentorshipRequestRepository.findAll().stream();

        filters.stream().filter(filter -> filter.isApplicable(incomeFilter))
                .forEach(filter -> filter.apply(requests, incomeFilter));

        return requests.map(requestMapper::toDto).toList();
    }

    public MentorshipRequestDto acceptRequest(Long id) {
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

        log.info("Запрос на менторство с id={} был принят", id);
        return requestMapper.toDto(entity);
    }

    public MentorshipRequestDto rejectRequest(Long id, RejectionDto rejectionDto) {
        MentorshipRequest request = getMentorshipRequestById(id);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getReason());

        MentorshipRequest entity = mentorshipRequestRepository.save(request);

        log.info("Запрос на менторство с id={} был отклонен с причиной={}", id, rejectionDto.getReason());
        return requestMapper.toDto(entity);
    }

    public MentorshipRequest getMentorshipRequestById(Long id) {
        return mentorshipRequestRepository.findById(id).orElseThrow(() -> {
            String message = "Запрос на менторство с id =" + id + " не найден";
            log.warn(message);
            return new EntityNotFoundException(message);
        });
    }
}
