package school.faang.user_service.controller.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.subscription.FollowRequestDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testFollowUser() throws Exception {
        FollowRequestDto followRequestDto = new FollowRequestDto();
        followRequestDto.setFollowerId(1);
        followRequestDto.setFolloweeId(2);

        mockMvc.perform(post("/subscription/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User followed successfully"));

        verify(subscriptionService, times(1)).followUser(followRequestDto);
    }

    @Test
    public void testUnfollowUser() throws Exception {
        FollowRequestDto followRequestDto = new FollowRequestDto();
        followRequestDto.setFollowerId(1);
        followRequestDto.setFolloweeId(2);

        mockMvc.perform(post("/subscription/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User unfollowed successfully"));

        verify(subscriptionService, times(1)).unfollowUser(followRequestDto);
    }

    @Test
    public void testGetFollowers() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("User1");
        userDto.setEmail("User1@email.com");
        List<UserDto> followers = List.of(userDto);

        when(subscriptionService.getFollowers(1L, null)).thenReturn(followers);

        mockMvc.perform(get("/subscription/followers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("User1"));

        verify(subscriptionService, times(1)).getFollowers(1L, null);
    }

    @Test
    public void testGetFollowersCount() throws Exception {
        when(subscriptionService.getFollowersCount(1L)).thenReturn(5);

        mockMvc.perform(get("/subscription/followers-number/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(subscriptionService, times(1)).getFollowersCount(1L);
    }

    @Test
    public void testGetFollowing() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setUsername("User2");
        List<UserDto> following = List.of(userDto);

        when(subscriptionService.getFollowing(1L, null)).thenReturn(following);

        mockMvc.perform(get("/subscription/following/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].username").value("User2"));

        verify(subscriptionService, times(1)).getFollowing(1L, null);
    }

    @Test
    public void testGetFollowingCount() throws Exception {
        when(subscriptionService.getFollowingCount(1L)).thenReturn(3);

        mockMvc.perform(get("/subscription/following-number/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        verify(subscriptionService, times(1)).getFollowingCount(1L);
    }
}