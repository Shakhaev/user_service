package school.faang.user_service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {

    private String accessKey;

    private String secretKey;

    private String endpoint;

    private String defaultAvatar;

}
