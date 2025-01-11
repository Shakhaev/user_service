package school.faang.user_service.service;

import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface SubscriptionFilter {

    boolean isApplicable(SubscriptionUserFilterDto filter);
    void apply(Stream<User> users, SubscriptionUserFilterDto filter);
}
