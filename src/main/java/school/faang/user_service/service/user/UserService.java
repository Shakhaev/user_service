package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.analyticsevent.SearchAppearanceEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.UserSaveException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final UserContext userContext;
    private final SearchAppearanceEventPublisher searchAppearanceEventPublisher;

    public User getUserById(Long id) {
        Optional<User> user = findUserByIdInDB(id);
        if (user.isPresent()) {
            log.info("User found with id: {}", id);
            return user.get();
        } else {
            log.error("User with id: {} not found", id);
            throw new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND, id));
        }
    }

    private Optional<User> findUserByIdInDB(Long id) {
        log.debug("Searching for user with id: {}", id);
        return userRepository.findById(id);
    }

    public boolean isUserExistByID(Long userId) {
        return userRepository.existsById(userId);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUserDtoByID(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND, userId));
        }
        SearchAppearanceEvent event = SearchAppearanceEvent.builder()
                .requesterId(userContext.getUserId())
                .foundUserId(userId)
//                .requestDateTime(LocalDateTime.now())
                .build();
        searchAppearanceEventPublisher.publish(event);
        return userMapper.toDto(optionalUser.get());
    }

    public void saveUser(User user) {
        try {
            log.info("Saving user: {}", user);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error saving user: {}", user, e);
            throw new UserSaveException(String.format(ErrorMessage.USER_SAVE_ERROR, user));
        }
    }

    public static List<Long> getNotExistingUserIds(UserRepository userRepository, List<Long> userIds) {
        List<Long> existingUserIds = userRepository.findAllById(userIds)
                .stream()
                .map(User::getId)
                .toList();

        return userIds.stream()
                .filter(id -> !existingUserIds.contains(id))
                .collect(Collectors.toList());
    }

    @Async("worker-pool")
    @Synchronized
    public void banUsers(List<Long> idForBanUsers) {
        List<User> usersToBan = userRepository.findAllById(idForBanUsers);
        usersToBan.forEach(user -> {
            user.setBanned(true);
            userRepository.save(user);
        });
        log.info("All found users were banned");
    }

    public List<UserDto> findByFilter(UserFilterDto filterDto) {
        var users = userRepository.findAll().stream();
        log.info("Applying filters to users. Filter params: {}", filterDto);
        return userFilters.stream()
                .filter(vacancyFilter -> vacancyFilter.isApplicable(filterDto))
                .flatMap(vacancyFilterActual -> vacancyFilterActual.apply(users, filterDto))
                .map(userMapper::toDto)
                .toList();
    }
}