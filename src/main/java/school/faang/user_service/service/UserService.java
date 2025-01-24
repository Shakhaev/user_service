package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> users;
    private final UserMapper userMapper;

    public void isUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователя с id " + userId + " не существует");
        }
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllByIds(@NotNull List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Невозможно получить пользователя"));
    }

    public List<UserDto> getPremiumUsers(UserFilterDto filter) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        users.stream()
                .filter(userFilter -> userFilter.isApplicable(filter))
                .forEach(userFilter -> userFilter.apply(premiumUsers, filter));

        return premiumUsers.map(userMapper::toDto).toList();
    }
}
