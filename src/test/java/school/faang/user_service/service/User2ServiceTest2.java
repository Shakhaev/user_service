package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.testing.EmailNotificationServiceException;
import school.faang.user_service.testing.EmailVerificationServiceImpl;
import school.faang.user_service.testing.User2;
import school.faang.user_service.testing.User2Repository;
import school.faang.user_service.testing.UserService2Impl;
import school.faang.user_service.testing.UserServiceException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class User2ServiceTest2 {

    @InjectMocks
    private UserService2Impl userService2;

    @Mock
    private User2Repository user2Repository;
    @Mock
    private EmailVerificationServiceImpl emailVerificationService;

    String firstName;
    String lastName;
    String email;
    String password;
    String repeatPassword;

    @BeforeEach
    void init() {
        firstName = "Parvin";
        lastName = "Etibarli";
        email = "test@test.com";
        password = "123";
        repeatPassword = "123";
    }

    @Test
    public void testCreateUser_whenEmailNotificationExceptionThrown_throwsUserServiceException() {
        // Arrange
        when(user2Repository.save(any())).thenReturn(true);
        doThrow(EmailNotificationServiceException.class)
                .when(emailVerificationService)
                .scheduleEmailConfirmation(any(User2.class));

        // Act
        Assertions.assertThrows(UserServiceException.class,
                () -> userService2.createUser(firstName, lastName, email, password, repeatPassword));

        // Assert
        verify(emailVerificationService, times(1)).scheduleEmailConfirmation(any(User2.class));
    }

    @Test
    public void foo() {
        // Arrange
        when(user2Repository.save(any())).thenThrow(RuntimeException.class);

        UserServiceException exception = Assertions.assertThrows(UserServiceException.class,
                () -> userService2.createUser(firstName, lastName, email, password, repeatPassword));
        System.out.println("-------------------------------------------");
        System.out.println(exception.getMessage());
        System.out.println("-------------------------------------------");
    }

    @Test
    public void testCreateUser_whenUserDetailsProvided_thenReturnsUserObject() {
        // Arrange
        when(user2Repository.save(any())).thenReturn(true);

        // Act
        User2 user = userService2.createUser(firstName, lastName, email, password, repeatPassword);

        // Assert
        Assertions.assertEquals(user.getFirstName(), firstName);
    }

    @Test
    public void testCreateUser_whenUserDetailsProvided_returnsUserObject() {
        // Act
        User2 user2 = userService2.createUser(firstName, lastName, email, password, repeatPassword);

        // Assert
        Assertions.assertNotNull(user2);
        Assertions.assertEquals(firstName, user2.getFirstName());
        Assertions.assertNotNull(user2.getId());
//        Mockito.verify(userService2, Mockito.times(1)).createUser(
//                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()
//        );
    }

    @Test
    public void testCreateUser_whenFirstNameIsEmpty_throwsIllegalArgumentException() {
        String firstName = "";
        String expectedExceptionMessage = "User's first name is empty";

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService2.createUser(firstName, lastName, email, password, repeatPassword));

        // Assert
        Assertions.assertEquals(expectedExceptionMessage, exception.getMessage());
    }

}
