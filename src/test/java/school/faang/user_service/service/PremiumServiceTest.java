package school.faang.user_service.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.constant.PaymentStatus;
import school.faang.user_service.constant.PremiumPeriod;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.res.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PremiumBadRequestException;
import school.faang.user_service.exception.PremiumNotFoundException;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.impl.PremiumServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    private final static Integer VALID_PREMIUM_PERIOD_DAYS = 30;
    private final static Long TEST_USER_ID = 1L;
    private final static Boolean USER_IS_ALREADY_PREMIUM_FLAG = true;
    private final static Boolean USER_IS_NOT_PREMIUM_FLAG = false;

    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private PremiumRepository premiumRepository;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    @Captor
    private ArgumentCaptor<PaymentRequest> paymentRequestCaptor;
    @Captor
    private ArgumentCaptor<Premium> premiumCaptor;

    private User testUser;
    private PremiumPeriod premiumPeriod;
    private Premium testPremium;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(TEST_USER_ID)
                .createdAt(LocalDateTime.now())
                .build();
        premiumPeriod = PremiumPeriod.fromDays(VALID_PREMIUM_PERIOD_DAYS);


        LocalDateTime now = LocalDateTime.now();
        testPremium = Premium.builder()
                .user(testUser)
                .startDate(now)
                .endDate(now.plusDays(premiumPeriod.getDays()))
                .build();
    }


    @Test
    void buyPremium_ValidRequest_Success() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(premiumRepository.existsByUserId(TEST_USER_ID)).thenReturn(USER_IS_NOT_PREMIUM_FLAG);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(getPatmentResponseEntity());
        when(premiumRepository.save(any(Premium.class))).thenReturn(testPremium);

        PremiumDto result = premiumService.buyPremium(TEST_USER_ID, premiumPeriod);

        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());

        verify(userRepository).findById(TEST_USER_ID);
        verify(premiumRepository).existsByUserId(TEST_USER_ID);
        verify(paymentServiceClient).sendPayment(paymentRequestCaptor.capture());
        verify(premiumRepository).save(premiumCaptor.capture());
        verify(premiumMapper).toDto(any(Premium.class));

        PaymentRequest capturedPaymentRequest = paymentRequestCaptor.getValue();
        assertEquals(premiumPeriod.getPrice(), capturedPaymentRequest.amount());
        assertEquals(premiumPeriod.getCurrency(), capturedPaymentRequest.currency());

        Premium capturedPremium = premiumCaptor.getValue();
        assertEquals(testUser, capturedPremium.getUser());
        assertNotNull(capturedPremium.getStartDate());
        assertNotNull(capturedPremium.getEndDate());
    }

    @Test
    void buyPremium_AlreadyPremiumUser_ThrowsException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(premiumRepository.existsByUserId(TEST_USER_ID)).thenReturn(USER_IS_ALREADY_PREMIUM_FLAG);
        assertThrows(PremiumBadRequestException.class,
                () -> premiumService.buyPremium(TEST_USER_ID, premiumPeriod));
    }

    @Test
    void buyPremium_NoUserFound_ThrowsException() {
        when(userRepository.findById(TEST_USER_ID))
                .thenReturn(Optional.empty());
        assertThrows(PremiumNotFoundException.class,
                () -> premiumService.buyPremium(TEST_USER_ID, premiumPeriod));
    }

    @Test
    void buyPremium_NullResponse_ThrowsException() {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(VALID_PREMIUM_PERIOD_DAYS);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(premiumRepository.existsByUserId(TEST_USER_ID)).thenReturn(false);
        when(paymentServiceClient.sendPayment(any())).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(PremiumBadRequestException.class,
                () -> premiumService.buyPremium(TEST_USER_ID, premiumPeriod));
    }

    private @NotNull ResponseEntity<PaymentResponse> getPatmentResponseEntity() {
        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                1,
                0L,
                premiumPeriod.getPrice(),
                premiumPeriod.getCurrency(),
                "OK"
        ));
    }
}
