package school.faang.user_service.rating.factory;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.rating.UserComparingDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.rating.comparators.LevelComparator;
import school.faang.user_service.rating.comparators.RatingPointsComparator;

import java.util.Comparator;

@Component
public class ComparatorFactory {
    public Comparator<User> getComparator(UserComparingDto userComparingDto) {
        if (userComparingDto.levelComparing()) {
            return new LevelComparator();
        }
        if (userComparingDto.pointsComparing()) {
            return new RatingPointsComparator();
        }
        return null;
    }
}