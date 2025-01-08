package school.faang.user_service.utility.validator.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorUtilsImplTest {

    private final ValidatorUtilsImpl validator = new ValidatorUtilsImpl();
    private String errorMessage;

    @BeforeEach
    void init() {
        errorMessage = "Value cannot be null";
    }

    @Test
    void testCheckNotNullPositive() {
        Event event = Event.builder().id(12L).build();

        assertDoesNotThrow(() -> validator.checkNotNull(event, errorMessage));
    }

    @Test
    void testCheckNotNullNegative() {
        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            validator.checkNotNull(null, errorMessage);
        });

        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void testCheckStringNotNullOrEmptyPositive() {
        String value = "new value";
        assertDoesNotThrow(() -> validator.checkStringNotNullOrEmpty(value, errorMessage));
    }

    @Test
    void testCheckStringNotNullOrEmptyNegativeNull() {
        String value = null;
        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            validator.checkStringNotNullOrEmpty(value, errorMessage);
        });

        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void testCheckStringNotNullOrEmptyNegativeBlank() {
        String value = "";
        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            validator.checkStringNotNullOrEmpty(value, errorMessage);
        });

        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void checkCollectionNotNullOrEmptyPositive() {
        Event event = Event.builder().id(12L).build();
        List<Event> list = List.of(event);

        assertDoesNotThrow(() -> validator.checkCollectionNotNullOrEmpty(list, errorMessage));
    }

    @Test
    void checkCollectionNotNullOrEmptyNegativeNull() {
        List<Event> list = null;

        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            validator.checkCollectionNotNullOrEmpty(list, errorMessage);
        });

        assertEquals(errorMessage, ex.getMessage());
    }
}