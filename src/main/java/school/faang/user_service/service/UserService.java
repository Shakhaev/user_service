package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserSearchResponse;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.model.jpa.User;
import school.faang.user_service.repository.jpa.UserRepository;
import school.faang.user_service.service.search.SearchUserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    private final SearchUserService searchUserService;
    private final UserMapper userMapper;

    public UserSearchResponse getUserById(long id) {
        User user = userRepo.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return userMapper.toSearchResponse(user);
    }

    public User findUserById(long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> getAllUsersByIds(List<Long> ids) {
        return userRepo.findAllById(ids);
    }


}