package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.async.AsyncConfig;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.user_cache.UserCacheDto;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.kafka.KafkaHeatFeedCacheProducer;
import school.faang.user_service.repository.cache.UserCacheRepository;
import school.faang.user_service.service.avatar.AvatarService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserCacheRepository userCacheRepository;
    private final AvatarService avatarService;
    private final AsyncConfig asyncConfig;
    private final UserMapper userMapper;
    private final UserService userService;
    private final KafkaHeatFeedCacheProducer kafkaHeatFeedCacheProducer;

    @Value(value = "${application.kafka.heat-feed-batch-size}")
    private int batchSize;

    @Transactional
    public void startHeatFeedCache() {
        int offset = 0;
        List<Long> batchUsersIds;
        do {
            batchUsersIds = userService.findActiveUsersIdsWithPagination(batchSize, offset);
            kafkaHeatFeedCacheProducer.send(batchUsersIds);
            offset += batchSize;

        } while (!batchUsersIds.isEmpty());
    }

    @Transactional
    public List<UserCacheDto> getUsersCachesDtos(List<Long> userIds) {
        List<User> users = userService.findAllUsersByIds(userIds);
        List<UserCacheDto> usersDto = userMapper.toListUserCacheDto(users);
        usersDto.forEach(user -> user.setProfilePicture(getProfilePicture(user.getUserId())));

        asyncConfig.taskExecutor().execute(() -> userCacheRepository.saveBatchUsersToCache(usersDto));
        log.info("Got {} users for cache ", usersDto.size());
        return usersDto;
    }

    @Transactional
    public void saveUsersToCache(List<Long> usersIds) {
        List<User> users = userService.findAllUsersByIds(usersIds);
        List<UserCacheDto> usersCaches = users.stream()
                .map(userMapper::toUserCacheDto)
                .peek(userCacheDto -> userCacheDto.setProfilePicture(getProfilePicture(userCacheDto.getUserId())))
                .toList();

        userCacheRepository.saveBatchUsersToCache(usersCaches);
        log.info("{} Users saved to cache", usersCaches.size());
    }

    private UserProfilePicDto getProfilePicture(long userId) {
        byte[] avatarData = avatarService.getUserAvatar(userId);
        return UserProfilePicDto.builder()
                .userId(userId)
                .profilePictureData(avatarData)
                .build();
    }
}
