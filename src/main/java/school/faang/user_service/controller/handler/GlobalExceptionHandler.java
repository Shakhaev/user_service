package school.faang.user_service.controller.handler;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.error.ErrorModel;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${service.name}")
    private String serviceName;

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel handleDataValidationException(DataValidationException ex) {
        return createError(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorModel handleResourceNotFoundException(ResourceNotFoundException ex) {
        return createError(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorModel handleEntityNotFoundException(EntityNotFoundException ex) {
        return createError(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorModel handleFeignException(FeignException ex) {
        return createError(ex.getMessage(), HttpStatus.BAD_GATEWAY.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorModel handleGenericException(Exception ex) {
        return createError("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ErrorModel createError(String message, int statusCode) {
        return new ErrorModel(message, statusCode, serviceName);
    }
}
