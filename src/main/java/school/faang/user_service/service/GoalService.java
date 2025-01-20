package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository repository;

    @Transactional
    public void removeUserFromGoal(Goal goal, long userId) {
        List<User> users = goal.getUsers();
        if (!users.removeIf(user -> user.getId() == userId)) {
            throw new IllegalArgumentException("Пользователь " + userId + " у цели не был найден");
        }
        goal.setUsers(users);
        if (goal.getUsers().size() < 1) {
            repository.deleteById(goal.getId());
        } else {
            repository.save(goal);
        }
    }
}
