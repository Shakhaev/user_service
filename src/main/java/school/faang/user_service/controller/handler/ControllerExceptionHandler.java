package school.faang.user_service.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import school.faang.user_service.controller.handler.error.ErrorMessage;
import school.faang.user_service.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private ErrorMessage buildErrorMessage(Exception exception, WebRequest webRequest, HttpStatus status) {
        String path = webRequest.getDescription(false).replace("uri=", "");
        return ErrorMessage.builder()
                .message(exception.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(value = UserWasNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage userWasNotFound(UserWasNotFoundException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DataValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleDataValidation(DataValidationException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MinioSaveException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleMinioSave(MinioSaveException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RecommendationRequestCreatedException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage handleRecommendationRequestCreated(RecommendationRequestCreatedException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = RequestAlreadyProcessedException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage handleRequestAlreadyProcessed(RequestAlreadyProcessedException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handleResourceNotFound(ResourceNotFoundException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = SkillNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handleSkillNotFound(SkillNotFoundException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage handleUserAlreadyExists(UserAlreadyExistsException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserGoalLimitExceededException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUserGoalLimitExceeded(UserGoalLimitExceededException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest, HttpStatus.BAD_REQUEST);
    }
}
