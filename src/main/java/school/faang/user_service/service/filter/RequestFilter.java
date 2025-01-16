package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public interface RequestFilter {
    boolean isApplicable(RequestFilterDto filters);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters);
}
