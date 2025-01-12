package school.faang.user_service.service.impl;

import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SubscriptionFilter;

import java.util.stream.Stream;

public class SubscriptionUserPageFilter implements SubscriptionFilter {
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        return true;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        int page = filter.getPage();
        int pageSize = filter.getPageSize();

        if (page == 0) {
            page = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return users.skip((long) (page - 1) * pageSize).limit(pageSize);
    }
}
