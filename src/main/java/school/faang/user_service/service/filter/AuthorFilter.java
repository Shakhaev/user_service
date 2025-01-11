package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class AuthorFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getAuthorPattern() != null;
    }

    @Override
    public void apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        requests = requests.filter(it -> it.getRequester().getUsername().matches(".*" + filters.getAuthorPattern() + ".*"));
    }
}
