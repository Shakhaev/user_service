package school.faang.user_service.utility.validator.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.utility.validator.ValidatorUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventValidatorUtilsImplTest {

    @Mock
    private ValidatorUtils utils;
    private final EventValidatorUtils validator = new EventValidatorUtilsImpl(utils);
    private String errorMessage;

    @BeforeEach
    void init() {
        errorMessage = "Data is invalid";
    }

    @Test
    void testCheckFutureDatePositive() {
        LocalDateTime futureDate = LocalDateTime.now().plusHours(1L);

        assertDoesNotThrow(() -> validator.checkFutureDate(futureDate, errorMessage));
    }

    @Test
    void testCheckFutureDateNegative() {
        LocalDateTime beforeDate = LocalDateTime.now().minusMinutes(1L);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            validator.checkFutureDate(beforeDate, errorMessage);
        });

        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void testCheckChronologyPositive() {
        LocalDateTime startDate = LocalDateTime.now().minusMinutes(1L);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1L);

        assertDoesNotThrow(() -> validator.checkChronology(startDate, endDate, errorMessage));
    }

    @Test
    void testCheckChronologyNegative() {
        LocalDateTime startDate = LocalDateTime.now().minusMinutes(1L);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1L);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            validator.checkChronology(endDate, startDate, errorMessage);
        });

        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void testCheckEnumValuePositve() {
        EventStatus status = EventStatus.PLANNED;
        EventStatus[] validStatuses = EventStatus.values();

        assertDoesNotThrow(() -> validator.checkEnumValue(status, validStatuses, errorMessage));
    }

    @Test
    void testCheckEnumTypePositve() {
        EventType type = EventType.MEETING;
        EventType[] types = EventType.values();

        assertDoesNotThrow(() -> validator.checkEnumValue(type, types, errorMessage));
    }

    @Test
    void testCheckEnumValueNotExists() {
        EventStatus status = null;
        EventStatus[] validStatuses = EventStatus.values();

        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            validator.checkEnumValue(status, validStatuses, errorMessage);
        });
        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void testCheckEnumTypeNotExists() {
        EventType type = null;
        EventType[] types = EventType.values();

        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            validator.checkEnumValue(type, types, errorMessage);
        });
        assertEquals(errorMessage, ex.getMessage());
    }
}