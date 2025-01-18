package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.UserController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Test
    void testGetPremiumUsers() throws Exception {
        UserService mockService = Mockito.mock(UserService.class);
        UserController controller = new UserController(mockService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        UserDto userDto = new UserDto(1L, "user1", "user1@example.com", "1234567890", "About me text", "City1", "Country1", true, true, "profilePicUrl");

        when(mockService.getPremiumUsers(any())).thenReturn(Collections.singletonList(userDto));

        mockMvc.perform(post("/users/premium")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"city\": \"City1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].premium").value(true));
    }

}
