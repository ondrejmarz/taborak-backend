package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.service.TourService;
import cz.ondrejmarz.taborakserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tours")
public class TourController {

    @Autowired
    private TourService tourService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        List<Tour> tours = tourService.getAllTours();
        return new ResponseEntity<>(tours, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable String id) {
        if (tourService.existsTourById(id)) {
            Tour tour = tourService.getTourById(id);
            return new ResponseEntity<>(tour, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Tour> createTour(@RequestBody Tour tour) {
        Tour createdTour = tourService.saveTour(tour);
        return new ResponseEntity<>(createdTour, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tour> updateTour(@PathVariable String id, @RequestBody Tour tour) {
        if (tourService.existsTourById(id)) {
            Tour updatedTour = tourService.saveTour(tour);
            return new ResponseEntity<>(updatedTour, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable String id) {
        if (tourService.existsTourById(id)) {
            tourService.deleteTour(tourService.getTourById(id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    /**
     *
     * Members of tour
     *
     **/

    @GetMapping("/{tourId}/members")
    public ResponseEntity<List<User>> getAllTourMembers(@PathVariable String tourId) {
        if (tourService.existsTourById(tourId)) {
            List<String> userIds = tourService.getTourById(tourId).getMembers();
            if (userIds != null) {
                List<User> users = userService.getAllUsersById(userIds);
                return new ResponseEntity<>(users, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{tourId}/members")
    public ResponseEntity<Tour> createTourMember(@PathVariable String tourId, @RequestBody User user) {
        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            tour.addMember(user.getUserId());
            tour.deleteApplication(user.getUserId());
            tourService.saveTour(tour);

            if (!userService.existsUserById(user.getUserId()))
                userService.saveUser(user);

            return new ResponseEntity<>(tour, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{tourId}/members/{userId}")
    public ResponseEntity<Tour> deleteTourMember(@PathVariable String tourId, @PathVariable String userId) {
        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            if (tour.getMembers().contains(userId)) {
                tour.deleteMember(userId);
                tourService.saveTour(tour);
            }
            return new ResponseEntity<>(tour, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    /**
     *
     * Applications to join tour
     *
     **/

    @GetMapping("/{tourId}/applications")
    public ResponseEntity<List<User>> getAllTourApplications(@PathVariable String tourId) {
        if (tourService.existsTourById(tourId)) {
            List<String> userIds = tourService.getTourById(tourId).getApplications();
            if (userIds != null) {
                List<User> users = userService.getAllUsersById(userIds);
                return new ResponseEntity<>(users, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{tourId}/applications")
    public ResponseEntity<Tour> createTourApplication(@PathVariable String tourId, @RequestBody User user) {
        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            tour.addApplication(user.getUserId());
            tourService.saveTour(tour);

            if (!userService.existsUserById(user.getUserId()))
                userService.saveUser(user);

            return new ResponseEntity<>(tour, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{tourId}/applications/{userId}")
    public ResponseEntity<Tour> deleteTourApplication(@PathVariable String tourId, @PathVariable String userId) {
        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            if (tour.getApplications().contains(userId)) {
                tour.deleteMember(userId);
                tourService.saveTour(tour);
            }
            return new ResponseEntity<>(tour, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

