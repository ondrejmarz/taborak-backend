package cz.ondrejmarz.taborakserver.service;

import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import cz.ondrejmarz.taborakserver.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourService {

    private final TourRepository tourRepository;

    private final DayPlanRepository dayPlanRepository;

    private final GroupRepository groupRepository;

    @Autowired
    public TourService(TourRepository tourRepository, DayPlanRepository dayPlanRepository, GroupRepository groupRepository) {
        this.tourRepository = tourRepository;
        this.dayPlanRepository = dayPlanRepository;
        this.groupRepository = groupRepository;
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll().collectList().block();
    }

    public Tour getTourById(String id) {
        return tourRepository.findById(id).block();
    }

    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour).block();
    }

    public void deleteTour(Tour tour) {
        groupRepository.deleteAllById(tour.getGroups()).block();
        dayPlanRepository.deleteAllById(tour.getDailyPrograms()).block();
        tourRepository.delete(tour).block();
    }

    public Boolean existsTourById(String id) {
        return tourRepository.existsById(id).block();
    }
}
