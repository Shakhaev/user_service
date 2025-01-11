package school.faang.user_service.service;

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
    @Mock
    private List<UserFilter> userFilters;

    User user1 = User.builder().id(1L).username("Mary").email("user@gmail.com").build();
    User user2 = User.builder().id(2L).username("John").email("admin@gmail.com").build();


    private static final long FOLLOWER_ID = 2;
    private static final long FOLLOWEE_ID = 1;

    @Test
    public void testFollowUserAndUnfollowUserValidationWithSameIds() {
        assertThrows(BusinessException.class, () ->
                subscriptionService.followUser(FOLLOWER_ID, FOLLOWER_ID)
        );
    }

    @Test
    public void testFollowUserAndUnfollowUserValidationWithFollowerNotExist() {
        mockUserExistById(false, FOLLOWER_ID);
        assertThrows(DataValidationException.class, () ->
                subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID)
        );
    }

    @Test
    public void testFollowUserAndUnfollowUserValidationWithFolloweeNotExist() {
        mockUserExistById(true, FOLLOWER_ID);
        mockUserExistById(false, FOLLOWEE_ID);
        assertThrows(DataValidationException.class, () ->
                subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID)
        );
    }

    @Test
    public void testFollowUserWhenSubscriptionExist() {
        mockFolloweeAndFollowerExist();
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);

        assertThrows(BusinessException.class, () ->
                subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID)
        );
        verify(subscriptionRepository, never()).followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnfollowUserWhenSubscriptionNotExist() {
        mockFolloweeAndFollowerExist();
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(false);

        assertThrows(BusinessException.class, () ->
                subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID)
        );
        verify(subscriptionRepository, never()).unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testFollowUserSuccessCase() {
        mockFolloweeAndFollowerExist();
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(false);

        subscriptionService.followUser(FOLLOWER_ID, FOLLOWEE_ID);
        verify(subscriptionRepository, times(1))
                .followUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testUnfollowUserSuccessCase() {
        mockFolloweeAndFollowerExist();
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID))
                .thenReturn(true);

        subscriptionService.unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);

        verify(subscriptionRepository, times(1))
                .unfollowUser(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @Test
    public void testGetFollowersWithBlankUserFilterDto() {
        createServiceWithFilters();
        mockUserExistById(true, FOLLOWEE_ID);
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID)).thenReturn(Stream.of(user1));
        SubscriptionUserDto subscriptionUserDto = subscriptionMapper.toDto(user1);

        List<SubscriptionUserDto> followers = subscriptionService.getFollowers(FOLLOWEE_ID, new UserFilterDto());

        assertEquals(1, followers.size());
        assertEquals(subscriptionUserDto, followers.get(0));
    }

    @Test
    public void testGetFollowersWithFewFilters() {
        createServiceWithFilters();
        mockUserExistById(true, FOLLOWEE_ID);
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID)).thenReturn(Stream.of(user1, user2));
        SubscriptionUserDto subscriptionUserDto = subscriptionMapper.toDto(user2);
        UserFilterDto dto = new UserFilterDto();
        dto.setNamePattern("Jo");
        dto.setEmailPattern("admin");

        List<SubscriptionUserDto> followers = subscriptionService.getFollowers(FOLLOWEE_ID, dto);
        assertEquals(1, followers.size());
        assertEquals(subscriptionUserDto, followers.get(0));
    }

    @Test
    public void testGetFollowersWithDifferentPage() {
        createServiceWithFilters();
        mockUserExistById(true, FOLLOWEE_ID);
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID)).thenReturn(Stream.of(user1, user2));
        SubscriptionUserDto subscriptionUserDto = subscriptionMapper.toDto(user2);
        UserFilterDto dto = new UserFilterDto();
        dto.setPage(2);
        dto.setPageSize(1);

        List<SubscriptionUserDto> followers = subscriptionService.getFollowers(FOLLOWEE_ID, dto);
        assertEquals(1, followers.size());
        assertEquals(subscriptionUserDto, followers.get(0));
    }

    @Test
    public void testGetFollowersWithPageLessThanOne() {
        createServiceWithFilters();
        mockUserExistById(true, FOLLOWEE_ID);
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID)).thenReturn(Stream.of(user1, user2));
        UserFilterDto dto = new UserFilterDto();
        dto.setPage(0);

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.getFollowers(FOLLOWEE_ID, dto)
        );
    }

    @Test
    public void testGetFollowersWithPageSizeLessThanOne() {
        createServiceWithFilters();
        mockUserExistById(true, FOLLOWEE_ID);
        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID)).thenReturn(Stream.of(user1, user2));
        UserFilterDto dto = new UserFilterDto();
        dto.setPageSize(0);

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.getFollowers(FOLLOWEE_ID, dto)
        );
    }

    @Test
    public void testGetFollowersCountSuccessCase() {
        mockUserExistById(true, FOLLOWEE_ID);

        subscriptionService.getFollowersCount(FOLLOWEE_ID);

        verify(
                subscriptionRepository,
                times(1)
        ).findFollowersAmountByFolloweeId(FOLLOWEE_ID);
    }

    @Test
    public void testGetFollowingSuccessCase() {
        createServiceWithFilters();
        mockUserExistById(true, FOLLOWER_ID);

        subscriptionService.getFollowing(FOLLOWER_ID, new UserFilterDto());
        verify(subscriptionRepository, times(1))
                .findByFollowerId(FOLLOWER_ID);
    }

    @Test
    public void testGetFollowingCountSuccessCase() {
        mockUserExistById(true, FOLLOWER_ID);
        subscriptionService.getFollowersCount(FOLLOWER_ID);

        verify(
                subscriptionRepository,
                times(1)
        ).findFollowersAmountByFolloweeId(FOLLOWER_ID);
    }

    private void mockFolloweeAndFollowerExist() {
        mockUserExistById(true, FOLLOWER_ID);
        mockUserExistById(true, FOLLOWEE_ID);
    }

    private void mockUserExistById(boolean exist, long id) {
        when(userRepository.existsById(id)).thenReturn(exist);
    }

    private void createServiceWithFilters() {
        userFilters = new ArrayList<>();
        userFilters.add(new UserNameFilter());
        userFilters.add(new UserEmailFilter());
        subscriptionService = new SubscriptionService(
                subscriptionRepository,
                userRepository,
                userFilters,
                subscriptionMapper
        );
    }
}
