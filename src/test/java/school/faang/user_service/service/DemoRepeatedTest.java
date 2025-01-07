package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInfo;

public class DemoRepeatedTest {

    CalculatorService calculatorService;

    @BeforeEach
    void beforeEachTestMethod() {
        calculatorService = new CalculatorService();
        System.out.println("Executing @BeforeEach method");
    }

    @RepeatedTest(3)
    @DisplayName("Division by zero")
    public void testIntegerDivision_WhenDividendIsDividedByZero_ShouldThrowArithmeticException(
            RepetitionInfo repetitionInfo,
            TestInfo testInfo) {
        System.out.println("Currently running test: " + testInfo.getDisplayName());
        System.out.println("Repetition number: " + repetitionInfo.getCurrentRepetition() + " of " + repetitionInfo.getTotalRepetitions());
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

}
