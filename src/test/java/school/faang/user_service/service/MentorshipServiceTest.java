package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.TestData.createTestUser;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Spy
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    void getMentors() {
        User user = createTestUser();
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<UserDto> userDtos = mentorshipService.getMentors(1L);

        assertEquals(user.getMentors().size(), userDtos.size());
        assertTrue(userDtos.containsAll(user.getMentors().stream().map(userMapper::toUserDto).toList()));
    }
}