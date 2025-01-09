package school.faang.user_service.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.entity.EntityNotFoundException;

import java.util.Map;

@RestControllerAdvice
public class EntityNotFoundExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, ?>> handleEntityNotFoundExceptionHandler(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(Map.of(
                "message:", e.getMessage(),
                "id", e.getEntityId()
        ));
    }
}
