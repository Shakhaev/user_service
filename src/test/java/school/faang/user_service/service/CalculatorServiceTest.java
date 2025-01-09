package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import school.faang.user_service.testing.CalculatorService;

import java.util.stream.Stream;

@DisplayName(value = "Test math operations in CalculatorService class")
public class CalculatorServiceTest {

    @BeforeAll
    static void setUp() {
        System.out.println("Before all");
    }

    CalculatorService calculatorService;

    @BeforeEach
    void up() {
        calculatorService = new CalculatorService();
    }

    @Test
    @DisplayName("4 / 2 = 2")
    public void testIntegerDivision_WhenFourIsDividedByTwo_ShouldReturnTwo() {
        // Arrange // Given
        int dividend = 4;
        int divisor = 2;
        int expectedResult = 2;

        // Act // When
        int result = calculatorService.integerDivision(dividend, divisor); // actual

        // Assert // Then
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Division by zero")
    public void testIntegerDivision_WhenDividendIsDividedByZero_ShouldThrowArithmeticException() {
        // Arrange
        int dividend = 5;
        int divisor = 0;
        String message = "/ by zero";

        // Act
        ArithmeticException arithmeticException = Assertions.assertThrows(ArithmeticException.class,
                () -> calculatorService.integerDivision(dividend, divisor));

        // Assert
        Assertions.assertEquals(message, arithmeticException.getMessage());
    }

    @Test
    @DisplayName("5 - 1 = 4")
    public void testIntegerSubtraction_WhenFiveIsSubtractedOne_ShouldReturnFour() {
        int actual = calculatorService.integerSubtraction(5, 1); // actual
        Assertions.assertEquals(4, actual);
    }

    @ParameterizedTest
//    @MethodSource
//    @CsvSource({
//            "33, 1, 32",
//            "55, 10, 45"
//    })
    @CsvFileSource(resources = "/integerSubtraction.csv")
    public void integerSubtraction(int minuend, int subtrahend, int expectedResult) {
        int actualResult = calculatorService.integerSubtraction(minuend, subtrahend);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    private static Stream<Arguments> integerSubtraction() {
        return Stream.of(
                Arguments.of(33, 1, 32),
                Arguments.of(5, 2, 3),
                Arguments.of(54, 10, 44)
        );
    }

}
