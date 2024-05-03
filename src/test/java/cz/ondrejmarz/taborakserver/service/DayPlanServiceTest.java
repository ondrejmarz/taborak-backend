package cz.ondrejmarz.taborakserver.service;

import cz.ondrejmarz.taborakserver.model.Activity;
import cz.ondrejmarz.taborakserver.model.DayPlan;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DayPlanServiceTest {

    @Mock
    private DayPlanRepository dayPlanRepository;

    @InjectMocks
    private DayPlanService dayPlanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllDayPlans() {
        DayPlan dayPlan1 = new DayPlan();
        dayPlan1.setDayId("ID1");
        dayPlan1.setDay("2024-05-01");

        DayPlan dayPlan2 = new DayPlan();
        dayPlan2.setDayId("ID2");
        dayPlan2.setDay("2024-05-02");

        List<DayPlan> dayPlans = Arrays.asList(dayPlan1, dayPlan2);

        when(dayPlanRepository.findAll()).thenReturn(Flux.fromIterable(dayPlans));

        List<DayPlan> result = dayPlanService.getAllDayPlans();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dayPlanRepository, times(1)).findAll();
    }

    @Test
    void getDayPlanById() {
        DayPlan dayPlan1 = new DayPlan();
        dayPlan1.setDayId("ID1");
        dayPlan1.setDay("2024-05-01");

        when(dayPlanRepository.findById(dayPlan1.getDayId())).thenReturn(Mono.just(dayPlan1));

        DayPlan result = dayPlanService.getDayPlanById(dayPlan1.getDayId());

        assertNotNull(result);
        assertEquals(dayPlan1.getDayId(), result.getDayId());
        assertEquals("2024-05-01", result.getDay());
        verify(dayPlanRepository, times(1)).findById(anyString());
    }

    @Test
    void saveDayPlan() {
        DayPlan dayPlan1 = new DayPlan();
        dayPlan1.setDayId("ID1");
        dayPlan1.setDay("2024-05-01");

        when(dayPlanRepository.save(dayPlan1)).thenReturn(Mono.just(dayPlan1));

        DayPlan result = dayPlanService.saveDayPlan(dayPlan1);

        assertNotNull(result);
        assertEquals(dayPlan1, result);
        verify(dayPlanRepository, times(1)).save(any());
    }

    @Test
    void deleteDayPlan() {
        DayPlan dayPlan1 = new DayPlan();
        dayPlan1.setDayId("ID1");
        dayPlan1.setDay("2024-05-01");

        dayPlanService.deleteDayPlan(dayPlan1);

        verify(dayPlanRepository, times(1)).delete(dayPlan1);
    }

    @Test
    void existsDayPlanById() {
        String dayPlanId = "ID1";

        when(dayPlanRepository.existsById(dayPlanId)).thenReturn(Mono.just(true));

        boolean result = dayPlanService.existsDayPlanById(dayPlanId);

        assertTrue(result);
    }

    @Test
    void fillActivities() {
        Activity wakeUp = new Activity(null, "Typ", "Popis", true, new Date(), new Date());
        DayPlan toFill = new DayPlan();
        toFill.setWakeUp(wakeUp);
        DayPlan exemplary = DayPlan.getDefaultDayPlan();

        DayPlan result = dayPlanService.fillActivities(toFill, exemplary);

        assertNotNull(result);
        assertEquals("Budíček", result.getWakeUp().getName());
    }

    @Test
    void findExemplaryDayPlan() {
        List<String> exemplaryDayPlansIds = Arrays.asList("1", "2", "3");

        DayPlan dayPlan1 = new DayPlan();
        dayPlan1.setDayId("ID1");
        dayPlan1.setDay("2024-05-01");

        DayPlan dayPlan2 = new DayPlan();
        dayPlan2.setDayId("ID2");
        dayPlan2.setDay("2024-05-02");

        DayPlan dayPlan3 = new DayPlan();
        dayPlan3.setDayId("ID3");
        dayPlan3.setDay("2024-04-01");

        List<DayPlan> dayPlans = Arrays.asList(dayPlan1, dayPlan2, dayPlan3);

        when(dayPlanRepository.findAllById(exemplaryDayPlansIds)).thenReturn(Flux.fromIterable(dayPlans));

        DayPlan result = dayPlanService.findExemplaryDayPlan(exemplaryDayPlansIds);

        assertNotNull(result);
        assertEquals("2024-05-02", result.getDay());
    }
}