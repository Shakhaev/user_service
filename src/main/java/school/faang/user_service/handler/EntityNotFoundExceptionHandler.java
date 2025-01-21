package school.faang.user_service.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.entity.EntityNotFoundExceptionWithId;

import java.util.Map;

@RestControllerAdvice
public class EntityNotFoundExceptionHandler {

    @ExceptionHandler(EntityNotFoundExceptionWithId.class)
    public ResponseEntity<Map<String, ?>> handleEntityNotFoundExceptionHandler(EntityNotFoundExceptionWithId e) {
        return ResponseEntity.status(404).body(Map.of(
                "message:", e.getMessage(),
                "id", e.getEntityId()
        ));
    }
}
