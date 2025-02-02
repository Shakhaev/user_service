package school.faang.user_service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.entity.User;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NamePatternFilterTest {

    private final NamePatternFilter filter = new NamePatternFilter();

    @Test
    void testApply_NameMatchesPattern() {
        User user1 = new User();
        user1.setUsername("Alice");
        User user2 = new User();
        user2.setUsername("Bob");

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("A.*"); // Имя начинается с "A"

        List<User> result = filter.apply(Stream.of(user1, user2), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getUsername());
    }
}
