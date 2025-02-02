package school.faang.user_service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.entity.User;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CityPatternFilterTest {

    private final CityPatternFilter filter = new CityPatternFilter();

    @Test
    public void testCityPatternFilter() {
        User user1 = new User();
        user1.setCity("New York");

        User user2 = new User();
        user2.setCity("Los Angeles");

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setCityPattern("New York");

        Stream<User> result = filter.apply(Stream.of(user1, user2), filterDto);
        List<User> filteredUsers = result.toList();

        assertEquals(1, filteredUsers.size());
        assertEquals("New York", filteredUsers.get(0).getCity());
    }
}
