package school.faang.user_service.utility.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractDataValidatorTest {
    private final AbstractDataValidator<Object> validator = new TestDataValidator();
    private String errorMessage;
    private Object testObject;

    @BeforeEach
    void init() {
        errorMessage = "Value cannot be null";
        testObject = new Object();
    }

    @Test
    void testCheckNotNull() {
        assertDoesNotThrow(() -> validator.checkNotNull(testObject, errorMessage));
    }

    @Test
    void testCheckNotNullIsNull() {
        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                validator.checkNotNull(null, errorMessage));

        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void testCheckStringNotNullOrEmpty() {
        String value = "test";
        assertDoesNotThrow(() -> validator.checkStringNotNullOrEmpty(value, errorMessage));
    }

    @Test
    void testCheckStringNotNullOrEmptyIsNull() {
        String value = null;
        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                validator.checkStringNotNullOrEmpty(value, errorMessage));

        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void testCheckStringNotNullOrEmptyIsBlank() {
        String value = "";
        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                validator.checkStringNotNullOrEmpty(value, errorMessage));

        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void testCollectionNotNullOrEmpty() {
        List<Object> list = List.of(testObject);

        assertDoesNotThrow(() -> validator.checkCollectionNotNullOrEmpty(list, errorMessage));
    }

    @Test
    void testCollectionNotNullOrEmptyIsNull() {
        List<Object> list = null;

        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                validator.checkCollectionNotNullOrEmpty(list, errorMessage));

        assertEquals(errorMessage, ex.getMessage());
    }
}
