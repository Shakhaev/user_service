package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.filters.user.impl.UserEmailFilter;
import school.faang.user_service.filters.user.impl.UserNameFilter;
import school.faang.user_service.mapper.SubscriptionMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private SubscriptionMapperImpl subscriptionMapper;
    @Spy
    private List<UserFilter> userFilters;

    private static final long FOLLOWER_ID = 2;
    private static final long FOLLOWEE_ID = 1;

    @BeforeEach
    public void setUp() {
        userFilters = new ArrayList<>();
        userFilters.add(mock(UserNameFilter.class));
        userFilters.add(mock(UserEmailFilter.class));
    }

    @Test
    public void testFollowUserAndUnfollowUserValidationWithSameIds() {
        assertThrows(BusinessException.class, () ->
                subscriptionService.followUser(FOLLOWER_ID, FOLLOWER_ID)
        );
    }

    @Test
    public void testFollowUserAndUnfollowUserValidationWithFollowerNotExist() {
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(false);
        assertThrows(DataValidationException.class, () ->
                subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID)
        );
    }

    @Test
    public void testFollowUserAndUnfollowUserValidationWithFolloweeNotExist() {
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(true);
        when(userRepository.existsById(FOLLOWEE_ID)).thenReturn(false);
        assertThrows(DataValidationException.class, () ->
                subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID)
        );
    }

    @Test
    public void testFollowUserWhenSubscriptionExist() {
        when(userRepository.existsById(FOLLOWEE_ID)).thenReturn(true);
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);

        assertThrows(BusinessException.class, () ->
                subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID)
        );
        verify(subscriptionRepository, never()).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnfollowUserWhenSubscriptionNotExist() {
        when(userRepository.existsById(FOLLOWEE_ID)).thenReturn(true);
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(false);

        assertThrows(BusinessException.class, () ->
                subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID)
        );
        verify(subscriptionRepository, never()).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testFollowUserSuccessCase() {
        when(userRepository.existsById(FOLLOWEE_ID)).thenReturn(true);
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(false);

        subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository, times(1))
                .followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnfollowUserSuccessCase() {
        when(userRepository.existsById(FOLLOWEE_ID)).thenReturn(true);
        when(userRepository.existsById(FOLLOWER_ID)).thenReturn(true);
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);

        subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionRepository, times(1))
                .unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testGetFollowersWithBlankUserFilterDto() {
        User user = User.builder().id(1L).email("user@gmail.com").build();
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID)).thenReturn(Stream.of(user));
        SubscriptionUserDto subscriptionUserDto = subscriptionMapper.toDto(user);

        List<SubscriptionUserDto> followers = subscriptionService.getFollowers(FOLLOWEE_ID, new UserFilterDto());

        assertEquals(1, followers.size());
        assertEquals(subscriptionUserDto, followers.get(0));
    }

    @Test
    public void testGetFollowersWithFewFilters() {
        User user1 = User.builder().id(1L).username("Mary").email("user@gmail.com").build();
        User user2 = User.builder().id(2L).username("John").email("admin@gmail.com").build();
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID)).thenReturn(Stream.of(user1, user2));
        SubscriptionUserDto subscriptionUserDto = subscriptionMapper.toDto(user2);
        UserFilterDto dto = new UserFilterDto();
        dto.setNamePattern("Jo");
        dto.setEmailPattern("admin");

        List<SubscriptionUserDto> followers = subscriptionService.getFollowers(FOLLOWEE_ID, dto);

        assertEquals(1, followers.size());
        assertEquals(subscriptionUserDto, followers.get(0));
    }
}
