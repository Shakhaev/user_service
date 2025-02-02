package school.faang.user_service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExperienceMaxFilterTest {

    private final ExperienceMaxFilter filter = new ExperienceMaxFilter();

    @Test
    void testApply_ExperienceBelowMax() {
        User user1 = new User();
        user1.setExperience(3);
        User user2 = new User();
        user2.setExperience(6);

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setExperienceMin(5); // Оставляем только тех, у кого опыт <= 5

        List<User> result = filter.apply(Stream.of(user1, user2), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getExperience());
    }
}
