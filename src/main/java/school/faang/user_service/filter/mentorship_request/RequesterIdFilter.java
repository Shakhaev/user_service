package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component("mentorshipRequesterIdFilter")
public class RequesterIdFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getRequesterId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> elements, RequestFilterDto filter) {
        return elements.filter(request -> Objects.equals(request.getRequester().getId(), filter.getRequesterId()));
    }
}
