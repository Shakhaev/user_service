package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecordsQuantityDto;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static java.lang.System.lineSeparator;
import static school.faang.user_service.exception.MessageError.USER_CANNOT_FOLLOW_TO_HIMSELF;
import static school.faang.user_service.exception.MessageError.USER_CANNOT_UNFOLLOW_FROM_HIMSELF;
import static school.faang.user_service.utils.Constants.API_VERSION_1;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION_1 + "/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public void followUser(long followerId, long followeeId) {

        log.info("Recieved HTTP request [POST] {} with parameters {} followerId = {}, followeeId = {}",
                API_VERSION_1 + "/subscription/follow",
                lineSeparator(),
                followerId,
                followeeId);

        if (followerId == followeeId) {
            log.error("Data validate exception: {}", USER_CANNOT_FOLLOW_TO_HIMSELF);
            throw new DataValidationException(USER_CANNOT_FOLLOW_TO_HIMSELF);
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(long followerId, long followeeId) {
        log.info("Recieved HTTP request [POST] {} with parameters {} followerId = {}, followeeId = {}",
                API_VERSION_1 + "/subscription/unfollow",
                lineSeparator(),
                followerId,
                followeeId);
        if (followerId == followeeId) {
            log.error("Data validate exception: {}", USER_CANNOT_UNFOLLOW_FROM_HIMSELF);
            throw new DataValidationException(USER_CANNOT_UNFOLLOW_FROM_HIMSELF);
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers")
    public List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filter) {
        log.info("Recieved HTTP request [POST] {} with parameters {} followeeId = {}",
                API_VERSION_1 + "/subscription/followers",
                lineSeparator(),
                followeeId);
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/following")
    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filter) {
        log.info("Recieved HTTP request [POST] {} with parameters {} followeeId = {}",
                API_VERSION_1 + "/subscription/following",
                lineSeparator(),
                followeeId);
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("/followers/count")
    public RecordsQuantityDto getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @GetMapping("/following/count")
    public RecordsQuantityDto getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
