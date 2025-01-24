package school.faang.user_service.service.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.config.dicebear.DiceBearApiConfig;
import school.faang.user_service.config.dicebear.DicebearStyleGenerator;
import school.faang.user_service.entity.avatar.AvatarType;
import school.faang.user_service.exception.DiceBearException;

@Slf4j
@RequiredArgsConstructor
@Service
public class DiceBearService {
    private final RestTemplate restTemplate;
    private final DiceBearApiConfig dicebearConfig;
    private final DicebearStyleGenerator styleGenerator;

    public byte[] generateAvatar(String filename, AvatarType type) {
        String url = String.format(
                "%s/%s/%s?seed=%s&width=%d&height=%d",
                dicebearConfig.getApiUrl(),
                styleGenerator.getRandomStyleString(),
                type.name().toLowerCase(),
                filename,
                type.getWidth(),
                type.getHeight());

        return getImageFromDicebear(url);
    }

    private byte[] getImageFromDicebear(String url) {
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, byte[].class);

            return response.getBody();
        } catch (RestClientException ex) {
            throw new DiceBearException("Failed to generate avatar");
        }
    }
}
