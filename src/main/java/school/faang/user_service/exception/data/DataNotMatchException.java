package school.faang.user_service.exception.data;

import lombok.Getter;

public class DataNotMatchException extends RuntimeException {
    private String message;
    @Getter
    private Object object;

    public DataNotMatchException(String message, Object o) {
        super(message);
        this.object = o;
    }
}
