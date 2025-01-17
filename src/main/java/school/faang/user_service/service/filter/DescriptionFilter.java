package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class DescriptionFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.descriptionPattern() != null;
    }

    @Override
    public void apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        requests = requests.filter(request -> request.getDescription().contains(filters.descriptionPattern()));
    }
}
