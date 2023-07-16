package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.EventParticipationService;

@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {
    @InjectMocks
    private EventParticipationController eventParticipationController;
    @Mock
    private EventParticipationService service;

    @Test
    void test_register_participant_should_success_register() {
        eventParticipationController.registerParticipant(1L, 1L);
        Mockito.verify(service, Mockito.times(1)).registerParticipant(1L, 1L);
    }

    @Test
    void test_register_participant_with_invalid_params_should_throw_exception() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationController.registerParticipant(-1L, -1L));
    }
}