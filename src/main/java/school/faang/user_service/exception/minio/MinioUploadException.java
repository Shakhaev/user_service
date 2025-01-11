package school.faang.user_service.exception.minio;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class MinioUploadException extends ApiException {
    private static final String MESSAGE = "Error uploading file to MinIO, file-name: %s, content-type: %s";

    public MinioUploadException(String fileName, String contentType) {
        super(MESSAGE, INTERNAL_SERVER_ERROR, fileName, contentType);
    }
}