package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest2 {

    UserService2 userService2;
    String firstName;
    String lastName;
    String email;
    String password;
    String repeatPassword;

    @BeforeEach
    void init() {
        userService2 = new UserService2Impl();
        firstName = "Parvin";
        lastName = "Etibarli";
        email = "test@test.com";
        password = "123";
        repeatPassword = "123";
    }

    @Test
    public void testCreateUser_whenUserDetailsProvided_returnsUserObject() {
        // Act
        User user = userService2.createUser(firstName, lastName, email, password, repeatPassword);

        // Assert
        Assertions.assertNotNull(user);
        Assertions.assertEquals(firstName, user.getFirstName());
        Assertions.assertNotNull(user.getId());
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
