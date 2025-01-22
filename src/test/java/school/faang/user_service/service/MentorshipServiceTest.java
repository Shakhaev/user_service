package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private UserRepository repository;
    @Mock
    private UserService userService;
    @InjectMocks
    private MentorshipService mentorshipService;

    private User user;

    @BeforeEach
    public void init() {
        UserMapper mapper = Mappers.getMapper(UserMapper.class);
        mentorshipService = new MentorshipService(repository, userService, mapper);
        user = User.builder().id(1L).username("Bob").build();
    }

    @Test
    public void getMentees_nullMenteesTest() {
        when(userService.getUser(1L)).thenReturn(user);

        Assert.assertEquals(new ArrayList<>(), mentorshipService.getMentees(1));
        Assert.assertTrue(mentorshipService.getMentees(1).size() == 0);
    }

    @Test
    public void getMentors_nullMentorsTest() {
        when(userService.getUser(1L)).thenReturn(user);

        Assert.assertEquals(new ArrayList(), mentorshipService.getMentors(1));
        Assert.assertTrue(mentorshipService.getMentors(1).size() == 0);
    }

    @Test
    public void getMentees_success() {
        List<User> mentees = new ArrayList<>();
        mentees.add(new User());
        mentees.add(new User());
        mentees.add(new User());
        user.setMentees(mentees);

        when(userService.getUser(1L)).thenReturn(user);

        Assert.assertTrue(mentorshipService.getMentees(1).size() == 3);
    }

    @Test
    public void getMentors_success() {
        List<User> mentors = new ArrayList<>();
        mentors.add(new User());
        mentors.add(new User());
        mentors.add(new User());
        user.setMentors(mentors);

        when(userService.getUser(1L)).thenReturn(user);

        Assert.assertTrue(mentorshipService.getMentors(1).size() == 3);
    }

    @Test
    public void deleteMentee_success() {
        List<User> mentees = new ArrayList<>();

        User target = new User();
        User anotherUser = new User();
        User anotherUser2 = new User();

        long targetId = 4L;
        target.setId(targetId);
        anotherUser.setId(2L);
        anotherUser2.setId(3L);

        mentees.add(target);
        mentees.add(anotherUser);
        mentees.add(anotherUser2);
        user.setMentees(mentees);

        when(userService.getUser(1L)).thenReturn(user);

        mentorshipService.deleteMentee(1, targetId);

        Assert.assertTrue(user.getMentees().stream().allMatch(mentee -> mentee.getId() != targetId));
        Assert.assertTrue(user.getMentees().size() == mentees.size() - 1);
    }

    @Test
    public void deleteMentor_success() {
        List<User> mentors = new ArrayList<>();

        User target = new User();
        User anotherUser = new User();
        User anotherUser2 = new User();

        long targetId = 4L;
        target.setId(targetId);
        anotherUser.setId(2L);
        anotherUser2.setId(3L);

        mentors.add(target);
        mentors.add(anotherUser);
        mentors.add(anotherUser2);
        user.setMentors(mentors);

        when(userService.getUser(1L)).thenReturn(user);

        mentorshipService.deleteMentor(1, targetId);

        Assert.assertTrue(user.getMentors().stream().allMatch(mentee -> mentee.getId() != targetId));
        Assert.assertTrue(user.getMentors().size() == mentors.size() - 1);
    }

    @Test
    public void deleteMentee_wrongId() {
        List<User> mentees = new ArrayList<>();

        User anotherUser = new User();
        User anotherUser2 = new User();

        anotherUser.setId(2L);
        anotherUser2.setId(3L);

        mentees.add(anotherUser);
        mentees.add(anotherUser2);
        user.setMentees(mentees);

        when(userService.getUser(1L)).thenReturn(user);

        mentorshipService.deleteMentee(1, 52);
        verify(repository).save(any());
    }

    @Test
    public void deleteMentor_wrongId() {
        List<User> mentors = new ArrayList<>();

        User anotherUser = new User();
        User anotherUser2 = new User();

        anotherUser.setId(2L);
        anotherUser2.setId(3L);

        mentors.add(anotherUser);
        mentors.add(anotherUser2);
        user.setMentors(mentors);

        when(userService.getUser(1L)).thenReturn(user);

        mentorshipService.deleteMentor(1, 52);
        verify(repository).save(any());
    }
}
