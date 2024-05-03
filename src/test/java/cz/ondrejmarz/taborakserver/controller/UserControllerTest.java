package cz.ondrejmarz.taborakserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import cz.ondrejmarz.taborakserver.repository.TourRepository;
import cz.ondrejmarz.taborakserver.repository.UserRepository;
import cz.ondrejmarz.taborakserver.service.TourService;
import cz.ondrejmarz.taborakserver.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthTokenFirebaseValidator authTokenValidator;

    @MockBean
    private DayPlanRepository dayPlanRepository;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private TourRepository tourRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TourService tourService;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsersById() throws Exception {
        List<String> userIds = Arrays.asList("userId1", "userId2", "userId3");
        List<User> expectedUsers = Arrays.asList(
                new User("userId1", "name1", "email1", null),
                new User("userId2", "name2", "email2", null),
                new User("userId3", "name3", "email3", null));

        when(authTokenValidator.validateTokenForUID(anyString())).thenReturn("userId2");
        when(userService.getAllUsersById(userIds)).thenReturn(expectedUsers);

        mockMvc.perform(get("/users")
                        .param("userIds", "userId1", "userId2", "userId3")
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].userId").value("userId1"))
                .andExpect(jsonPath("$[0].userName").value("name1"))
                .andExpect(jsonPath("$[0].email").value("email1"))
                .andExpect(jsonPath("$[1].userId").value("userId2"))
                .andExpect(jsonPath("$[1].userName").value("name2"))
                .andExpect(jsonPath("$[1].email").value("email2"))
                .andExpect(jsonPath("$[2].userId").value("userId3"))
                .andExpect(jsonPath("$[2].userName").value("name3"))
                .andExpect(jsonPath("$[2].email").value("email3"));

        verify(userService, times(1)).getAllUsersById(userIds);
    }

    @Test
    void saveUser() throws Exception {
        User newUser = new User("userId1", "name1", "email1", null);

        when(authTokenValidator.validateTokenForUID(anyString())).thenReturn("userId2");
        when(userService.existsUserById(newUser.getUserId())).thenReturn(false);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newUser))
                        .header("Authorization", "token"))
                .andExpect(status().isCreated());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}