package school.faang.user_service.requestformentoring.helper.filters;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

public class FilterByAuthorRequest implements FilterRequest {

    @Override
    public List<MentorshipRequest> filterMenReq(RequestFilterDto filterDto, List<MentorshipRequest> menReq) {
        return menReq.stream().filter(mentorshipRequest -> mentorshipRequest.getReceiver().getId()
                .equals(filterDto.getReceiverId())).toList();
    }
}
