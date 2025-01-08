package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BannerUserTest {

    @InjectMocks
    private BannerService bannerService;

    @Mock
    private UserRepository userRepository;

    @Captor
    ArgumentCaptor<List<Long>> usersIdsCaptor = ArgumentCaptor.forClass((Class<List<Long>>) (Class) ArrayList.class);

    @Test
    public void testToBanUser_ShouldNotFoundUsersForBan() {
        when(userRepository.findAllByIds(usersIdsCaptor.capture()))
                .thenReturn(Optional.empty());

        bannerService.banUsers(List.of(1L));

        verify(userRepository, times(1)).findAllByIds(usersIdsCaptor.getValue());
    }

    @Test
    public void testToBanUser_ShouldFoundUsersForBan() {
        List<Long> usersIds = List.of(1L, 2L);
        List<User> users = List.of(
                User.builder().banned(false).id(1L).build(),
                User.builder().banned(false).id(2L).build());
        when(userRepository.findAllByIds(usersIdsCaptor.capture()))
                .thenReturn(Optional.of(users));

        bannerService.banUsers(usersIds);

        verify(userRepository, times(1)).findAllByIds(usersIds);
    }

    @Test
    public void testToSetBanToUsers_ShouldUpdateBanStatus() {
        List<User> users = List.of(
                User.builder().banned(false).id(1L).build(),
                User.builder().banned(false).id(2L).build());

        bannerService.setBanToUsers(users);

        for (User user : users) {
            assertTrue(user.isBanned());
        }
    }
}
