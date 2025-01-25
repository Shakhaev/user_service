package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class MessageDto {
    private boolean status;
    private String message;
    private int code;
}
