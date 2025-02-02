package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.DeactivatedUserDto;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testDeactivateUserSuccessful() throws Exception {
        String dateTimeString = "2025-02-01 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTimeFromString = LocalDateTime.parse(dateTimeString, formatter);

        long deactivatedUserId = 1L;

        DeactivatedUserDto deactivatedUserDto = new DeactivatedUserDto();
        deactivatedUserDto.setId(deactivatedUserId);
        deactivatedUserDto.setUsername("JohnDoe");
        deactivatedUserDto.setEmail("johndoe@example.com");
        deactivatedUserDto.setPhone("1234567890");
        deactivatedUserDto.setAboutMe("About John Doe");
        deactivatedUserDto.setActive(true);
        deactivatedUserDto.setCity("New York");
        deactivatedUserDto.setCountryId(1L);
        deactivatedUserDto.setExperience(2);
        deactivatedUserDto.setCreatedAt(localDateTimeFromString);
        deactivatedUserDto.setUpdatedAt(localDateTimeFromString);

        Mockito.when(userService.deactivateUser(deactivatedUserId)).thenReturn(deactivatedUserDto);

        mockMvc.perform(patch("/api/v1/users/{id}/deactivate", deactivatedUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deactivatedUserDto.getId()))
                .andExpect(jsonPath("$.username").value(deactivatedUserDto.getUsername()))
                .andExpect(jsonPath("$.email").value(deactivatedUserDto.getEmail()))
                .andExpect(jsonPath("$.phone").value(deactivatedUserDto.getPhone()))
                .andExpect(jsonPath("$.aboutMe").value(deactivatedUserDto.getAboutMe()))
                .andExpect(jsonPath("$.active").value(deactivatedUserDto.isActive()))
                .andExpect(jsonPath("$.city").value(deactivatedUserDto.getCity()))
                .andExpect(jsonPath("$.countryId").value(deactivatedUserDto.getCountryId()))
                .andExpect(jsonPath("$.experience").value(deactivatedUserDto.getExperience()))
                .andExpect(jsonPath("$.createdAt").value(dateTimeString))
                .andExpect(jsonPath("$.updatedAt").value(dateTimeString));

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(userService, Mockito.times(1)).deactivateUser(argumentCaptor.capture());

        Assertions.assertEquals(deactivatedUserId, argumentCaptor.getValue());
    }
}