package school.faang.user_service.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import school.faang.user_service.dto.error.ErrorResponse;
import school.faang.user_service.exception.*;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {UserWasNotFoundException.class, ResourceNotFoundException.class, SkillNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse userWasNotFound(UserWasNotFoundException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest);
    }

    @ExceptionHandler(value = {DataValidationException.class, UserGoalLimitExceededException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidation(DataValidationException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest);
    }

    @ExceptionHandler(value = MinioSaveException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMinioSave(MinioSaveException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest);
    }

    @ExceptionHandler(value = {RecommendationRequestCreatedException.class, RequestAlreadyProcessedException.class, UserAlreadyExistsException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleRecommendationRequestCreated(RecommendationRequestCreatedException exception, WebRequest webRequest) {
        return buildErrorMessage(exception, webRequest);
    }

    private ErrorResponse buildErrorMessage(Exception exception, WebRequest webRequest) {
        String path = webRequest.getDescription(false).replace("uri=", "");
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .path(path)
                .build();
    }
}
