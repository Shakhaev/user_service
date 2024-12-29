package school.faang.user_service.exception.entity;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {
    private String message;
    @Getter
    private Long entityId;

    public EntityNotFoundException(String message, Long entityId) {
        super(message);
        this.entityId = entityId;
    }
}
