package school.faang.user_service.utility.validator.impl;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFiltersDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EventFiltersDtoValidatorTest {
    private final EventFiltersDtoValidator validator = new EventFiltersDtoValidator();

    @Test
    void testValidate() {
        EventFiltersDto dto = EventFiltersDto.builder()
                .title("title")
                .startDate(LocalDateTime.now().plusHours(1L))
                .location("location")
                .ownerName("ownerName")
                .build();

        assertDoesNotThrow(() -> validator.validate(dto));
    }
}
