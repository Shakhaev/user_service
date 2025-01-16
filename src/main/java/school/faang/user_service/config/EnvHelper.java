package school.faang.user_service.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

public class EnvHelper {
    public static void loadVariables() {
        Dotenv dotenv = Dotenv.configure()
            .directory("src/main/resources")
            .ignoreIfMissing()
            .load();

        for (DotenvEntry entry : dotenv.entries()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
