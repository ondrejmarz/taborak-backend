package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.TourUser;
import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import cz.ondrejmarz.taborakserver.repository.TourRepository;
import cz.ondrejmarz.taborakserver.repository.UserRepository;
import cz.ondrejmarz.taborakserver.service.TourService;
import cz.ondrejmarz.taborakserver.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TourMemberController.class)
class TourMemberControllerTest {

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
    private TourMemberController tourMemberController;

    @Test
    void getAllTourMembers() throws Exception {
        String tourId = "ID1";
        List<TourUser> tourUsers = Arrays.asList(new TourUser("userID1", "name1", "email1", "major"), new TourUser("userID2", "name2", "email2", "minor"));
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), List.of("userID1", "userID2"), null, null, null);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);
        when(userService.getAllTourUsersById(eq(Arrays.asList("userID1", "userID2")), eq(tourId))).thenReturn(tourUsers);

        mockMvc.perform(get("/tours/{tourId}/members", tourId)
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userId").value("userID1"))
                .andExpect(jsonPath("$[0].userName").value("name1"))
                .andExpect(jsonPath("$[0].email").value("email1"))
                .andExpect(jsonPath("$[0].role").value("major"))
                .andExpect(jsonPath("$[1].userId").value("userID2"))
                .andExpect(jsonPath("$[1].userName").value("name2"))
                .andExpect(jsonPath("$[1].email").value("email2"))
                .andExpect(jsonPath("$[1].role").value("minor"));
    }

    @Test
    void getUserTourRole() throws Exception {
        String tourId = "1";
        String userId = "userID";
        String role = "major";

        when(authTokenValidator.validateTokenForUID(anyString())).thenReturn(userId);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(userService.existsUserById(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "name", "email", Collections.singletonMap(tourId, role)));

        mockMvc.perform(get("/tours/{tourId}/members/{userId}/role", tourId, userId)
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(content().string(role));
    }

    @Test
    void createTourMember() throws Exception {
        String tourId = "ID1";
        String userId = "userID1";
        List<String> applications = new ArrayList<>(Collections.singletonList(userId));
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), null, applications, null, null);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);
        when(userService.existsUserById(userId)).thenReturn(true);

        mockMvc.perform(put("/tours/{tourId}/members/{userId}", tourId, userId)
                        .header("Authorization", "token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tourId").value(tourId))
                .andExpect(jsonPath("$.title").value("Title1"))
                .andExpect(jsonPath("$.topic").value("Topic1"))
                .andExpect(jsonPath("$.description").value("Description1"))
                .andExpect(jsonPath("$.members[?(@ == '" + userId + "')]").exists())
                .andExpect(jsonPath("$.applications").isEmpty());

        verify(tourService, times(1)).saveTour(argThat(savedTour -> savedTour.getApplications().isEmpty() && savedTour.getMembers().contains(userId)));
    }

    @Test
    void setTourUserRole() throws Exception {
        String tourId = "ID1";
        String userId = "userID1";
        String role = "major";
        List<String> userList = new ArrayList<>(Collections.singletonList(userId));
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), userList, null, null, null);
        User user = new User(userId, "userName1", "email1", null);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);
        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(put("/tours/{tourId}/members/{userId}/role", tourId, userId)
                        .header("Authorization", "token")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(role))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(content().string(role));

        verify(userService, times(1)).saveUser(argThat(savedUser -> savedUser.getRoles().containsKey(tourId) && Objects.equals(savedUser.getRoles().get(tourId), role)));
    }

    @Test
    void deleteLastTourMember() throws Exception {
        String tourId = "ID1";
        String userId = "userID1";
        List<String> memberList = new ArrayList<>(Collections.singletonList(userId));
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), memberList, null, null, null);


        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);

        mockMvc.perform(delete("/tours/{tourId}/members/{userId}", tourId, userId)
                        .header("Authorization", "token"))
                .andExpect(status().isOk());

        verify(tourService, times(1)).deleteTour(tour);
    }

    @Test
    void deleteTourMemberWithMultipleMembers() throws Exception {
        String tourId = "ID1";
        String userIdToRemove = "userID1";
        String userIdRemaining = "userID2";
        List<String> memberList = new ArrayList<>(Arrays.asList(userIdToRemove, userIdRemaining));
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), memberList, null, null, null);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);

        mockMvc.perform(delete("/tours/{tourId}/members/{userId}", tourId, userIdToRemove)
                        .header("Authorization", "token"))
                .andExpect(status().isOk());

        verify(tourService, never()).deleteTour(tour);
        assertTrue(tour.getMembers().contains(userIdRemaining));
        assertFalse(tour.getMembers().contains(userIdToRemove));
    }
}