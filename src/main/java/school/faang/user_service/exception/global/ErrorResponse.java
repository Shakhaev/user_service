package school.faang.user_service.exception.global;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class ErrorResponse {
    private HttpStatus httpStatus;
    private String message;
    private String error;
    private Map<String, String> errors;
}
