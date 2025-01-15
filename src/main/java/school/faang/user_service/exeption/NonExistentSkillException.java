package school.faang.user_service.exeption;

public class NonExistentSkillException extends RuntimeException {
    public NonExistentSkillException(String message) {
        super(message);
    }
}