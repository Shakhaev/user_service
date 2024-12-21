package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.events.ProfileViewEvent.ProfileViewEvent;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final ProfileViewEventPublisher profileViewEventPublisher;

    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new DataValidationException("User by ID is not found"));
        Long currentUserId = userContext.getUserId();
        if (currentUserId != 0 && currentUserId != userId) {
            ProfileViewEvent profileViewEvent = ProfileViewEvent.builder()
                    .authorId(userId)
                    .viewerName((userRepository.findById(currentUserId)
                            .map(User::getUsername)
                            .orElseThrow(() -> new DataValidationException("Header is Blank"))))
                    .localDateTime(LocalDateTime.now())
                    .build();
            profileViewEventPublisher.publish(profileViewEvent);
            log.info("Profile view event sent for userId: {} by viewerId: {}", userId, currentUserId);
        } else {
            log.info("UserId is 0, skipping event publication.");
        }
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }
}