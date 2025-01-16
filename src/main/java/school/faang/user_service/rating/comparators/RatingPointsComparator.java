package school.faang.user_service.rating.comparators;

import school.faang.user_service.entity.User;

import java.util.Comparator;

public class RatingPointsComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return Integer.compare(o2.getRatingPoints(), o1.getRatingPoints());
    }
}
