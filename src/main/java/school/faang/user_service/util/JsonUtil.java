package school.faang.user_service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class JsonUtil {
    private final ObjectMapper objectMapper;

    public String convertToJson(Serializable request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }
}
