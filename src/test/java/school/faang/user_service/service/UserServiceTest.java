package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    UserRepository userRepository;
    List<UserFilter> userFilters;
    UserMapperImpl userMapper;
    UserService userService;

    @BeforeEach
    void init() {
        userRepository = mock(UserRepository.class);
        userFilters = new ArrayList<>();
        userFilters.add(mock(UserFilter.class));
        userFilters.add(mock(UserFilter.class));
        userMapper = spy(UserMapperImpl.class);
        userService = new UserService(userRepository, userFilters, userMapper);
    }

    @Test
    void getPremiumUsers_ShouldReturn() {
        User correctUser = new User();
        User wrongUser = new User();
        when(userRepository.findPremiumUsers())
                .thenReturn(Stream.of(correctUser, wrongUser));
        when(any(UserFilter.class).isApplicable(any()))
                .thenReturn(true);
        when(userFilters.get(0).apply(any(), any()))
                .thenReturn(Stream.of(correctUser, wrongUser));
        when(userFilters.get(1).apply(any(), any()))
                .thenReturn(Stream.of(correctUser));
        assertEquals(List.of(userMapper.toDto(correctUser)), userService.getPremiumUsers(new UserFilterDto()));
    }
}