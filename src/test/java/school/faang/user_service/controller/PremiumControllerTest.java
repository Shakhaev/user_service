package school.faang.user_service.controller;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.constant.PremiumPeriod;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.service.PremiumService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {
    private final static Integer MONTH_PREMIUM_PERIOD_DAYS = 30;
    private final static Long USER_ID = 1L;

    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PremiumController premiumController;

    @Test
    public void testBuyPremiumSuccess() {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(MONTH_PREMIUM_PERIOD_DAYS);


//        when(premiumService.buyPremium(USER_ID, premiumPeriod)).thenReturn(inputPremiumDto);

        PremiumDto result = premiumController.buyPremium(premiumPeriod.getDays());

        assertNotNull(result);
//        assertEquals(inputPremiumDto, result);
        verify(premiumService.buyPremium(USER_ID, premiumPeriod));

    }
}
