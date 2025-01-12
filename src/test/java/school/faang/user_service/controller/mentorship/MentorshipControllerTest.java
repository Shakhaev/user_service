package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


// import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class MentorshipControllerTest {

    @Mock
    private MentorshipService mentorshipService;
//
//    @Mock
//    private MentorshipRepository mentorshipRepository;

//    @Spy
//    private UserMapperImpl userMapperImpl;

    @InjectMocks
    private MentorshipController mentorshipController;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this); // delete

    }

    @Test
    void testGetMentees() {
        // Arrange
        // long mentorId = 1L;


        //        List<User> users = Arrays.asList(new User(1L, "John"), new User(2L, "Jane"));


        List<UserDto> users = Arrays.asList(
                UserDto.builder().id(0L).username("user0").build(),
                UserDto.builder().id(1L).username("user1").build(),
                UserDto.builder().id(2L).username("user2").build(),
                UserDto.builder().id(3L).username("user3").build(),
                UserDto.builder().id(4L).username("user4").build(),
                UserDto.builder().id(5L).username("user5").build(),
                UserDto.builder().id(6L).username("user6").build(),
                UserDto.builder().id(7L).username("user7").build(),
                UserDto.builder().id(8L).username("user8").build(),
                UserDto.builder().id(9L).username("user9").build()
        );

        List<UserDto> mentees = Arrays.asList(
                users.get(9), users.get(8), users.get(7), users.get(6)
        );

        users.get(3).setMentees(mentees);
        users.get(5).setMentees(mentees);

        // change to repo
        when(mentorshipService.getMentees(3L)).thenReturn(mentees);
//        when(mentorshipRepository.findMenteesByUserId(3L)).thenReturn(users);
//        when(mentorshipRepository.findById(3L)).thenReturn(users);

//        System.out.println(mentorshipController.getMentees(3));


        // Act
        // ResponseEntity<List<UserDto>> response = mentorshipController.getMentees(mentorId);
        List<UserDto> response = mentorshipController.getMentees(3);

        System.out.println(response);

        // Assert
        assertNotNull(response);
        assertEquals(4, response.size());
//        assertTrue(response.getBody().containsAll(users));
    }
}