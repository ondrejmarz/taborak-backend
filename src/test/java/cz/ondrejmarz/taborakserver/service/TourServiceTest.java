package cz.ondrejmarz.taborakserver.service;

import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import cz.ondrejmarz.taborakserver.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private DayPlanRepository dayPlanRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private TourService tourService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTours() {
        Tour tour1 = new Tour("ID1", "Title1", "Topic1", "Description1", Date.from(Instant.now()), Date.from(Instant.now()), null, null, null, null);
        Tour tour2 = new Tour("ID2", "Title2", "Topic2", "Description2", Date.from(Instant.now()), Date.from(Instant.now()), null, null, null, null);
        List<Tour> tours = Arrays.asList(tour1, tour2);

        when(tourRepository.findAll()).thenReturn(Flux.fromIterable(tours));

        List<Tour> result = tourService.getAllTours();

        assertNotNull(result);
        assertEquals(tours, result);
        verify(tourRepository, times(1)).findAll();
    }

    @Test
    void getTourById() {
        String tour1Id = "ID1";

        Tour tour1 = new Tour(tour1Id, "Title1", "Topic1", "Description1", Date.from(Instant.now()), Date.from(Instant.now()), null, null, null, null);

        when(tourRepository.findById(tour1Id)).thenReturn(Mono.just(tour1));

        Tour result = tourService.getTourById(tour1Id);

        assertNotNull(result);
        assertEquals(tour1, result);
        verify(tourRepository, times(1)).findById(tour1Id);
    }

    @Test
    void saveTour() {
        Tour tour1 = new Tour("ID1", "Title1", "Topic1", "Description1", Date.from(Instant.now()), Date.from(Instant.now()), null, null, null, null);

        when(tourRepository.save(tour1)).thenReturn(Mono.just(tour1));

        Tour result = tourService.saveTour(tour1);

        assertNotNull(result);
        assertEquals(tour1, result);
        verify(tourRepository, times(1)).save(tour1);
    }

    @Test
    void deleteTour() {
        String tour1Id = "ID1";

        Tour tour1 = new Tour(tour1Id, "Title1", "Topic1", "Description1", Date.from(Instant.now()), Date.from(Instant.now()), null, null, null, null);

        when(tourRepository.existsById(tour1Id)).thenReturn(Mono.just(true));
        when(tourRepository.delete(tour1)).thenReturn(Mono.empty());
        when(groupRepository.deleteAllById(tour1.getGroups())).thenReturn(Mono.empty());
        when(dayPlanRepository.deleteAllById(tour1.getDailyPrograms())).thenReturn(Mono.empty());

        tourService.deleteTour(tour1);

        verify(tourRepository, times(1)).delete(tour1);
        verify(groupRepository, times(1)).deleteAllById(tour1.getGroups());
        verify(dayPlanRepository, times(1)).deleteAllById(tour1.getDailyPrograms());
    }

    @Test
    void existsTourById() {
        String tour1Id = "ID1";

        when(tourRepository.existsById(tour1Id)).thenReturn(Mono.just(true));

        Boolean result = tourService.existsTourById(tour1Id);

        assertEquals(true, result);
        verify(tourRepository, times(1)).existsById(tour1Id);
    }
}