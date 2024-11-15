package school.faang.user_service.service.mentorship;

import java.util.List;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.mentorship.MentorshipRequest;

public interface MentorshipRequestService {

  MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

  List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto);

  MentorshipRequest findById(long requestId);

  MentorshipRequestDto acceptRequest(long id);

  MentorshipRequestDto rejectRequest(long id, RejectionDto rejectionDto);
}
