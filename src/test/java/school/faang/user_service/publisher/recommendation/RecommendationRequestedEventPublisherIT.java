package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Testcontainers
class RecommendationRequestedEventPublisherIT {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("testDb")
                    .withUsername("testUser")
                    .withPassword("testPass");
    @Container
    private static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>("redis:latest")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
    }

    @Value("${spring.data.redis.channels.recommendation-requested-channel.name}")
    private String channel;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisMessageListenerContainer redisContainer;
    @Autowired
    private RecommendationRequestService recommendationRequestService;

    @Test
    @Transactional
    @Sql(scripts = "classpath:sql/recommendation_requested_test_data.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testUserCreationAndMessagePublish() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        TestMessageListener testMessageListener = new TestMessageListener(countDownLatch);
        redisContainer.addMessageListener(testMessageListener, new ChannelTopic(channel));

        RecommendationRequestDto requestDto = new RecommendationRequestDto()
                .setMessage("test message")
                .setSkillIds(List.of(1L, 2L))
                .setRequesterId(1L)
                .setReceiverId(2L);
        RecommendationRequestDto result = recommendationRequestService.create(requestDto);
        if (!countDownLatch.await(5L, TimeUnit.SECONDS)) {
            fail("Message processing timed out.");
        }

        RecommendationRequestDto requestFoundById = recommendationRequestService.getRequest(result.getId());
        assertEquals(result, requestFoundById);

        Message message = testMessageListener.getReceivedMessages().get(0);
        RecommendationRequestedEvent requestedEvent = objectMapper.readValue(message.getBody(),
                RecommendationRequestedEvent.class);
        assertEquals(result.getId(), requestedEvent.getId());
        assertEquals(result.getRequesterId(), requestedEvent.getRequesterId());
        assertEquals(result.getReceiverId(), requestedEvent.getReceiverId());
    }

    @Getter
    @RequiredArgsConstructor
    private static class TestMessageListener implements MessageListener {
        private final CountDownLatch countDownLatch;

        private List<Message> receivedMessages = new ArrayList<>();

        @Override
        public void onMessage(Message message, byte[] pattern) {
            receivedMessages.add(message);
            countDownLatch.countDown();
        }
    }
}