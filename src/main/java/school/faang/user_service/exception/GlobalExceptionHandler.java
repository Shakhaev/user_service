package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.premium.MessageDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PremiumAlreadyExistsException.class)
    public ResponseEntity<MessageDto> handlePremiumAlreadyExistsException(PremiumAlreadyExistsException e) {
        return new ResponseEntity<>(new MessageDto(false, e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentServiceException.class)
    public ResponseEntity<MessageDto> handlePremiumAlreadyExistsException(PaymentServiceException e) {
        return new ResponseEntity<>(new MessageDto(false, e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

}
