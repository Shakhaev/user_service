package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService service;

    public void followUser(long followerId, long followeeId) throws DataValidationException {
        service.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
        service.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return service.getFollowers(followeeId, filter);
    }
}