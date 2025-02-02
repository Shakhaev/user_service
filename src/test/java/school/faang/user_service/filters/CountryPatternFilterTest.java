package school.faang.user_service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.entity.Country;
import school.faang.user_service.dto.entity.User;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CountryPatternFilterTest {

    private final CountryPatternFilter filter = new CountryPatternFilter();

    @Test
    void testApply_CountryMatches() {
        Country usa = new Country(1L, "USA", null);
        Country canada = new Country(2L, "Canada", null);

        User user1 = new User();
        user1.setCountry(usa);
        User user2 = new User();
        user2.setCountry(canada);

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setCountryPattern("USA");

        List<User> result = filter.apply(Stream.of(user1, user2), filterDto).toList();

        assertEquals(1, result.size());
        assertEquals("USA", result.get(0).getCountry().getTitle());
    }
}