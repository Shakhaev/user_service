package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserIdListIsEmptyException;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class UserDomainService {
    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @PublishEvent(returnedType = User.class)
    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @PublishEvent(returnedType = User.class)
    @Transactional(readOnly = true)
    public List<User> findAllByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<User> findAllWithActivePremiumInRange(long offset, long limit) {
        return userRepository.findAllWithActivePremiumInRange(offset, limit);
    }

    @Transactional(readOnly = true)
    public List<Long> getOnlyActiveUserIdsFromList(List<Long> ids) {
        if (isEmpty(ids)) {
            throw new UserIdListIsEmptyException();
        }
        return userRepository.findActiveUserIds(ids);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
