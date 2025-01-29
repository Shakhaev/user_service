package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
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

    public UserReadDto getUser(long userId) {
        return userMapper.toDto(getById(userId));
    }

    public List<UserReadDto> getUsersByIds(List<Long> ids) {
        return getAllByIds(ids).stream()
                .map(userMapper::toDto)
                .toList();
    }
}
