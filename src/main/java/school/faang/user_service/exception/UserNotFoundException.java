package school.faang.user_service.exception;

public class UserNotFoundException extends IllegalArgumentException {
    public UserNotFoundException(String message) {
        super("Пользователь уже зарегистрирован!");
    }
}
