package cz.ondrejmarz.taborakserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.DayPlan;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.TourUser;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import cz.ondrejmarz.taborakserver.repository.TourRepository;
import cz.ondrejmarz.taborakserver.repository.UserRepository;
import cz.ondrejmarz.taborakserver.service.DayPlanService;
import cz.ondrejmarz.taborakserver.service.TourService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DayPlanController.class)
class DayPlanControllerTest {

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
    private DayPlanService dayPlanService;

    @InjectMocks
    private DayPlanController dayPlanController;

    @Test
    void getTourDayPlan() throws Exception {
        String tourId = "existingTourId";
        String day = "2024-04-04";
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, null, List.of("dailyID1", "dailyID2"));

        DayPlan dayPlan1 = new DayPlan();
        dayPlan1.setDayId("dailyID1");
        dayPlan1.setDay("error");

        DayPlan dayPlan2 = new DayPlan();
        dayPlan2.setDayId("dailyID2");
        dayPlan2.setDay(day);

        when(authTokenValidator.validateToken(any(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);
        when(dayPlanService.existsDayPlanById(any())).thenReturn(true);
        when(dayPlanService.getDayPlanById(dayPlan1.getDayId())).thenReturn(dayPlan1);
        when(dayPlanService.getDayPlanById(dayPlan2.getDayId())).thenReturn(dayPlan2);

        mockMvc.perform(get("/tours/{tourId}/calendar/{day}", tourId, day)
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.day").value(day));
    }

    @Test
    void testGetTourDayPlanNotExistsOrInvalidFormat() throws Exception {
        String tourId = "nonExistingTourId";
        String invalidDay = "invalidDate";
        String authHeader = "token";

        when(authTokenValidator.validateToken(authHeader, tourId, List.of("guest", "major", "minor", "troop")))
                .thenReturn(true);

        mockMvc.perform(get("/tours/{tourId}/calendar/{day}", tourId, invalidDay)
                        .header("Authorization", authHeader))
                .andExpect(status().isPreconditionFailed());

        when(tourService.existsTourById(tourId)).thenReturn(false);

        mockMvc.perform(get("/tours/{tourId}/calendar/{day}", tourId, "2024-04-30")
                        .header("Authorization", authHeader))
                .andExpect(status().isNotFound());
    }

    @Test
    void creatDayPlan() throws Exception {
        String tourId = "validTourID";
        String dayPlanId = "ID1";
        String day = "2024-05-01";

        DayPlan dayPlan1 = new DayPlan();
        dayPlan1.setDayId(dayPlanId);
        dayPlan1.setDay(day);

        DayPlan dayPlan2 = new DayPlan();
        dayPlan2.setDayId("ID2");
        dayPlan2.setDay("2024-04-01");

        List<String> dailyPrograms = new ArrayList<>(Collections.singletonList(dayPlan2.getDayId()));
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, null, dailyPrograms);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);
        when(dayPlanService.findExemplaryDayPlan(anyList())).thenReturn(dayPlan2);
        when(dayPlanService.fillActivities(any(DayPlan.class), any(DayPlan.class))).thenReturn(dayPlan1);
        when(dayPlanService.saveDayPlan(any(DayPlan.class))).thenReturn(dayPlan1);

        mockMvc.perform(post("/tours/{tourId}/calendar/{day}", tourId, day)
                        .header("Authorization", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dayPlan1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.day").value(day));

        verify(dayPlanService, times(1)).fillActivities(any(DayPlan.class), any(DayPlan.class));
        verify(dayPlanService, times(1)).saveDayPlan(any(DayPlan.class));
        verify(tourService, times(1)).saveTour(
                argThat(savedTour ->
                        savedTour.getDailyPrograms().size() == 2
                )
        );
    }

    @Test
    void updateDayPlan() throws Exception {
        String tourId = "validTourID";
        String dayPlanId = "ID1";
        String day = "2024-05-01";

        DayPlan dayPlan1 = new DayPlan();
        dayPlan1.setDayId(dayPlanId);
        dayPlan1.setDay(day);

        List<String> dailyPrograms = new ArrayList<>(Collections.singletonList(dayPlan1.getDayId()));
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, null, dailyPrograms);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);
        when(dayPlanService.existsDayPlanById(dayPlanId)).thenReturn(true);
        when(dayPlanService.getDayPlanById(dayPlanId)).thenReturn(dayPlan1);
        when(dayPlanService.saveDayPlan(any(DayPlan.class))).thenReturn(dayPlan1);

        mockMvc.perform(put("/tours/{tourId}/calendar/{day}", tourId, day)
                        .header("Authorization", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dayPlan1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.day").value(day));

        verify(dayPlanService, times(1)).saveDayPlan(any(DayPlan.class));
    }

    @Test
    void deleteDayPlan() throws Exception {
        String tourId = "validTourID";
        String dayPlanId = "ID1";
        String day = "2024-05-01";

        DayPlan dayPlan1 = new DayPlan();
        dayPlan1.setDayId(dayPlanId);
        dayPlan1.setDay(day);

        List<String> dailyPrograms = new ArrayList<>(Collections.singletonList(dayPlanId));
        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, null, dailyPrograms);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);
        when(dayPlanService.existsDayPlanById(dayPlanId)).thenReturn(true);
        when(dayPlanService.getDayPlanById(dayPlanId)).thenReturn(dayPlan1);

        mockMvc.perform(delete("/tours/{tourId}/calendar/{day}", tourId, day)
                        .header("Authorization", "token"))
                .andExpect(status().isNoContent());

        verify(tourService, times(1)).saveTour(argThat(savedTour -> savedTour.getDailyPrograms().isEmpty()));
        verify(dayPlanService, times(1)).deleteDayPlan(any(DayPlan.class));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

