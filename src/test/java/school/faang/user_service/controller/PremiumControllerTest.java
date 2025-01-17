package school.faang.user_service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.constant.PremiumPeriod;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.exception.PremiumBadRequestException;
import school.faang.user_service.service.PremiumService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {
    private final static Integer VALID_PREMIUM_PERIOD_DAYS = 30;
    private static final Integer INVALID_PREMIUM_PERIOD_DAYS = 10;
    private final static Long TEST_USER_ID = 1L;

    @Mock
    private PremiumService premiumService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private PremiumController premiumController;

    @Test
    @DisplayName("buyPremium_ValidRequest_ReturnsPremiumDto")
    void buyPremium_ValidRequest_ReturnsPremiumDto() {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(VALID_PREMIUM_PERIOD_DAYS);
        PremiumDto inputPremiumDto = createTestPremiumDto();

        when(userContext.getUserId()).thenReturn(TEST_USER_ID);
        when(premiumService.buyPremium(TEST_USER_ID, premiumPeriod)).thenReturn(inputPremiumDto);

        PremiumDto result = premiumController.buyPremium(premiumPeriod.getDays());

        assertEquals(inputPremiumDto, result);
        assertNotNull(result);
        verify(premiumService).buyPremium(TEST_USER_ID, premiumPeriod);
    }

    @Test
    void buyPremium_InvalidDays_ThrowsException() {
        assertThrows(
                PremiumBadRequestException.class,
                () -> premiumController.buyPremium(INVALID_PREMIUM_PERIOD_DAYS)
        );

        verifyNoInteractions(premiumService);
    }

    @Test
    void buyPremium_UserIdNotSet_ThrowsException() {
        when(userContext.getUserId()).thenThrow(new IllegalArgumentException("User id is missing"));

        assertThrows(
                IllegalArgumentException.class,
                () -> premiumController.buyPremium(VALID_PREMIUM_PERIOD_DAYS));
        verifyNoInteractions(premiumService);
    }

    private PremiumDto createTestPremiumDto() {
        return PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .startDate(null)
                .endDate(null)
                .build();
    }
}
