package school.faang.user_service.exception.entity;

import lombok.Getter;

public class EntityNotFoundExceptionWithId extends RuntimeException {
    private String message;
    @Getter
    private Long entityId;

    public EntityNotFoundExceptionWithId(String message, Long entityId) {
        super(message);
        this.entityId = entityId;
    }
}
