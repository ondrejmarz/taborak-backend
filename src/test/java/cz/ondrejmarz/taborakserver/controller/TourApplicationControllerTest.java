package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.TourUser;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TourApplicationController.class)
class TourApplicationControllerTest {

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
    private TourApplicationController tourApplicationsController;

    @Test
    void getAllTourApplications() throws Exception {
        String tourId = "1";
        List<TourUser> tourUsers = Arrays.asList(new TourUser("userID1", "name1", "email1", "major"), new TourUser("userID2", "name2", "email2", "minor"));
        Tour tour = new Tour("ID1", "Title1", "Topic1", "Description1", new Date(), new Date(), null, List.of("userID1", "userID2"), null, null);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);
        when(userService.getAllTourUsersById(eq(Arrays.asList("userID1", "userID2")), eq(tourId))).thenReturn(tourUsers);

        mockMvc.perform(get("/tours/{tourId}/applications", tourId)
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
    void createTourApplication() throws Exception {
        String tourId = "ID1";
        String userId = "userID1";
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, null, null);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);
        when(userService.existsUserById(userId)).thenReturn(true);

        mockMvc.perform(put("/tours/{tourId}/applications/{userId}", tourId, userId)
                        .header("Authorization", "token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tourId").value(tourId))
                .andExpect(jsonPath("$.title").value("Title1"))
                .andExpect(jsonPath("$.topic").value("Topic1"))
                .andExpect(jsonPath("$.description").value("Description1"))
                .andExpect(jsonPath("$.applications[?(@ == '" + userId + "')]").exists())
                .andExpect(jsonPath("$.members").isEmpty());

        verify(tourService, times(1)).saveTour(argThat(savedTour -> savedTour.getApplications().contains(userId)));
    }

    @Test
    void deleteTourApplication() throws Exception {
        String tourId = "ID1";
        String userIdToRemove = "userID1";
        String userIdRemaining = "userID2";
        List<String> memberList = new ArrayList<>(Arrays.asList(userIdToRemove, userIdRemaining));
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), memberList, null, null, null);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);

        mockMvc.perform(delete("/tours/{tourId}/applications/{userId}", tourId, userIdToRemove)
                        .header("Authorization", "token"))
                .andExpect(status().isOk());

        verify(tourService, never()).saveTour(argThat(savedTour -> !savedTour.getApplications().contains(userIdToRemove) && savedTour.getApplications().contains(userIdRemaining)));
    }
}