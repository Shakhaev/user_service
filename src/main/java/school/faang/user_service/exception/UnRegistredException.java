package school.faang.user_service.exception;

public class UnRegistredException extends IllegalArgumentException {
    public UnRegistredException(String message) {
        super("Пользователь не ЗАРЕГИСТРИРОВАН на событие!");
    }
}
