package school.faang.user_service.exeption;

public class NoSkillsFoundException extends RuntimeException {
    public NoSkillsFoundException(String message) {
        super(message);
    }
}
