package school.faang.user_service.config.dicebear;

import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public class DicebearStyleGenerator {
    private final Random random;

    public String getRandomStyleString() {
        DicebearStyle[] styles = DicebearStyle.values();
        return styles[random.nextInt(styles.length)]
                .toString()
                .toLowerCase();
    }
}
