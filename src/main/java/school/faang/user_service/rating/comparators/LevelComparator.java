package school.faang.user_service.rating.comparators;

import school.faang.user_service.entity.User;

import java.util.Comparator;

public class LevelComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return Integer.compare(o2.getExperience(), o1.getExperience());
    }
}
