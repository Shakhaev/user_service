package school.faang.user_service.controller;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.PremiumService;

@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {

    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PremiumController premiumController;

}
