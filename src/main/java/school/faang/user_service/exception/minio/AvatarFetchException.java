package school.faang.user_service.exception.minio;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class AvatarFetchException extends ApiException {
    private static final String MESSAGE = "Failed to fetch avatar from DiceBear API for style %s";

    public AvatarFetchException(String styleName) {
        super(MESSAGE, INTERNAL_SERVER_ERROR, styleName);
    }
}
