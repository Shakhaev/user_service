package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private final UserRepository userRepository;

    @Transactional
    public void banUsers(List<Long> userIds) {
        log.info("user ban started! user ids: {}", userIds);
        userRepository.findAllByIds(userIds)
                .ifPresentOrElse(
                        this::setBanToUsers,
                        () -> log.info("users by ids: {}, not found!", userIds)
                );
    }

    @Transactional
    public void setBanToUsers(List<User> users) {
        users.forEach(user -> user.setBanned(true));
        log.info("users success banned");
    }
}
