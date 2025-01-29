package school.faang.user_service.service.impl;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;

import java.util.Collections;
import java.util.HashMap;

import static school.faang.user_service.constant.UserErrorMessages.USER_WITH_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new FeignException.NotFound(String.format(USER_WITH_ID_NOT_FOUND, userId),
                        Request.create(Request.HttpMethod.GET, "url", new HashMap<>(), null, new RequestTemplate()),
                        null,
                        Collections.emptyMap()
                ));
    }
}
