package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        return userRepository.findPremiumUsers()
                .filter(user -> applyFilter(user, filterDto))
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    private boolean applyFilter(User user, UserFilterDto filterDto) {
        return (filterDto.getUsername() == null || user.getUsername().contains(filterDto.getUsername())) &&
                (filterDto.getEmail() == null || user.getEmail().contains(filterDto.getEmail())) &&
                (filterDto.getCity() == null || user.getCity().equals(filterDto.getCity())) &&
                (filterDto.getActive() == null || user.isActive() == filterDto.getActive());
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .city(user.getCity())
                .active(user.isActive())
                .build();
    }
}
