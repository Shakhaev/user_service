package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.MentorshipRequestFilter;

import java.util.Objects;
import java.util.stream.Stream;

@Component("mentorshipReceiverIdFilter")
public class ReceiverIdFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> elements, RequestFilterDto filter) {
        return elements.filter(request -> Objects.equals(request.getReceiver().getId(), filter.getReceiverId()));
    }
}
