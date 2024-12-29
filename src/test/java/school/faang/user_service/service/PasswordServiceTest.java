package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordService passwordService;

    @Test
    void encodePassword_shouldReturnEncodedPassword() {

        String rawPassword = "myPassword123";
        String encodedPassword = "$2a$10$DowJgEczAAmwLk3GJjJzJ.ycQUefoqHbNTDdWTvsA9DqNCSeLaD2S"; // Мокированный результат

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        String result = passwordService.encodePassword(rawPassword);

        assertNotNull(result);
        assertEquals(encodedPassword, result);
        verify(passwordEncoder, times(1)).encode(rawPassword);  // Проверяем, что метод encode был вызван 1 раз
    }
}
