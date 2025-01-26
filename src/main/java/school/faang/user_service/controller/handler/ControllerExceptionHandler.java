package school.faang.user_service.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import school.faang.user_service.dto.error.ErrorResponse;
import school.faang.user_service.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private ErrorResponse buildErrorMessage(Exception exception, WebRequest webRequest, HttpStatus status) {
        String path = webRequest.getDescription(false).replace("uri=", "");
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(value = UserWasNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse userWasNotFound(UserWasNotFoundException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DataValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidation(DataValidationException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MinioSaveException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMinioSave(MinioSaveException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RecommendationRequestCreatedException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleRecommendationRequestCreated(RecommendationRequestCreatedException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = RequestAlreadyProcessedException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleRequestAlreadyProcessed(RequestAlreadyProcessedException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = SkillNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleSkillNotFound(SkillNotFoundException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExists(UserAlreadyExistsException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserGoalLimitExceededException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserGoalLimitExceeded(UserGoalLimitExceededException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.BAD_REQUEST);
    }
}
