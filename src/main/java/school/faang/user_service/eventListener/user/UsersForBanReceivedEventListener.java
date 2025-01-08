package school.faang.user_service.eventListener.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.BanUsersDto;
import school.faang.user_service.service.user.BannerService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsersForBanReceivedEventListener implements MessageListener {

    private final BannerService bannerService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        BanUsersDto users;
        try {
            users = objectMapper.readValue(message.getBody(), BanUsersDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("received users for ban, users ids: {}", users.usersIds());
        bannerService.banUsers(users.usersIds());
    }
}
