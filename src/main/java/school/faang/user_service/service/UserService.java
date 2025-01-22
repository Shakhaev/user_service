package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private List<UserFilter> users;
    private UserMapper userMapper;

    public List<UserDto> getPremiumUsers(UserFilterDto filter) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        users.stream()
                .filter(userFilter -> userFilter.isApplicable(filter))
                .forEach(userFilter -> userFilter.apply(premiumUsers, filter));

        return premiumUsers.map(userMapper::toDto).toList();
    }
}
