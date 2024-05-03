package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import cz.ondrejmarz.taborakserver.repository.TourRepository;
import cz.ondrejmarz.taborakserver.repository.UserRepository;
import cz.ondrejmarz.taborakserver.service.TourService;
import cz.ondrejmarz.taborakserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(TourController.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DayPlanRepository dayPlanRepository;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private TourRepository tourRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private TourService tourService;

    @MockBean
    private AuthTokenFirebaseValidator authTokenValidator;

    @InjectMocks
    private TourController tourController;

    TourControllerTest() {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTours() throws Exception {
        Tour tour1 = new Tour("ID1", "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, null, null);
        Tour tour2 = new Tour("ID2", "Title2", "Topic2", "Description2", new Date(), new Date(), null, null, null, null);
        List<Tour> tours = Arrays.asList(tour1, tour2);

        when(tourService.getAllTours()).thenReturn(tours);

        mockMvc.perform(get("/tours"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].tourId").value("ID1"))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[0].topic").value("Topic1"))
                .andExpect(jsonPath("$[0].description").value("Description1"))
                .andExpect(jsonPath("$[1].tourId").value("ID2"))
                .andExpect(jsonPath("$[1].title").value("Title2"))
                .andExpect(jsonPath("$[1].topic").value("Topic2"))
                .andExpect(jsonPath("$[1].description").value("Description2"));
    }

    @Test
    void getTourById() throws Exception {
        String tourId = "ID1";
        Tour tour = new Tour("ID1", "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, null, null);

        when(authTokenValidator.validateToken(any(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);

        mockMvc.perform(get("/tours/{tourId}", tourId)
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tourId").value("ID1"))
                .andExpect(jsonPath("$.title").value("Title1"))
                .andExpect(jsonPath("$.topic").value("Topic1"))
                .andExpect(jsonPath("$.description").value("Description1"));
    }

    @Test
    void createTour() throws Exception {
        User user = new User("userID", "name", "email", null);
        Tour tour = new Tour("ID3", "Title3", "Topic3", "Description3", new Date(), new Date(), List.of(user.getUserId()), null, null, null);
        String requestBody = "{\"tourId\":\"ID3\",\"title\":\"Title3\",\"topic\":\"Topic3\",\"description\":\"Description3\",\"members\":[\"userID\"]}";

        when(tourService.saveTour(any())).thenReturn(tour);
        when(userService.getUserById(user.getUserId())).thenReturn(user);

        mockMvc.perform(post("/tours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tourId").value("ID3"))
                .andExpect(jsonPath("$.title").value("Title3"))
                .andExpect(jsonPath("$.topic").value("Topic3"))
                .andExpect(jsonPath("$.description").value("Description3"));
    }

    @Test
    void createTourWithoutMember() throws Exception {
        Tour tourWithoutMembers = new Tour("ID4", "Title4", "Topic4", "Description4", new Date(), new Date(), null, null, null, null);
        String requestBody = "{\"tourId\":\"ID4\",\"title\":\"Title4\",\"topic\":\"Topic4\",\"description\":\"Description4\"}";


        mockMvc.perform(post("/tours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    void updateTour() throws Exception {
        String tourId = "ID1";
        Tour tour = new Tour("ID1", "Updated Title", "Updated Topic", "Updated Description", new Date(), new Date(), null, null, null, null);
        String requestBody = "{\"tourId\":\"ID1\",\"title\":\"Updated Title\",\"topic\":\"Updated Topic\",\"description\":\"Updated Description\"}";

        when(authTokenValidator.validateToken(any(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.saveTour(any())).thenReturn(tour);

        mockMvc.perform(put("/tours/{tourId}", tourId)
                        .header("Authorization", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tourId").value("ID1"))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.topic").value("Updated Topic"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void deleteTour() throws Exception {
        String tourId = "ID1";

        when(authTokenValidator.validateToken(any(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);

        mockMvc.perform(delete("/tours/{tourId}", tourId)
                        .header("Authorization", "token"))
                .andExpect(status().isNoContent());
    }
}