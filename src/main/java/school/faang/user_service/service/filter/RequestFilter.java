package school.faang.user_service.service.filter;

import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface RequestFilter {
    boolean isApplicable(MentorshipRequestFilterDto filters);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filters);
}
