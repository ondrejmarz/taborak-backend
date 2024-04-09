package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.TourUser;
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
    private AuthTokenFirebaseValidator authTokenValidator;

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
    public ResponseEntity<List<TourUser>> getAllTourMembers(
            @PathVariable String tourId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("guest", "major", "minor", "troop")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            List<String> userIds = tourService.getTourById(tourId).getMembers();
            if (userIds != null) {
                List<TourUser> tourUsers = userService.getAllTourUsersById(userIds, tourId);
                return new ResponseEntity<>(tourUsers, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{tourId}/members/{userId}")
    public ResponseEntity<String> getUserTourRole(@PathVariable String tourId, @PathVariable String userId) {
        if (tourService.existsTourById(tourId)) {
            if (userService.existsUserById(userId)) {
                String userTourRole =
                        userService.getUserById(userId).getRoles().get(tourId) == null ?
                        userService.getUserById(userId).getRoles().get(tourId) : "guest";
                return new ResponseEntity<>(userTourRole, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{tourId}/members/{userId}")
    public ResponseEntity<Tour> createTourMember(
            @PathVariable String tourId,
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            if (userService.existsUserById(userId))
                tour.addMember(userId);
            tour.deleteApplication(userId);
            tourService.saveTour(tour);
            return new ResponseEntity<>(tour, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * Sets role of user in tour
     *
     */
    @PutMapping("/{tourId}/members/{userId}/role")
    public ResponseEntity<String> setTourUserRole(
            @PathVariable String tourId,
            @PathVariable String userId,
            @RequestBody String role,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            if (tourService.getTourById(tourId).getMembers().contains(userId)) {
                User user = userService.getUserById(userId);
                user.addRole(tourId, role);
                userService.saveUser(user);
                return new ResponseEntity<>(role, HttpStatus.OK);
            }
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
    public ResponseEntity<List<TourUser>> getAllTourApplications(
            @PathVariable String tourId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("guest", "major", "minor", "troop")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            List<String> userIds = tourService.getTourById(tourId).getApplications();
            if (userIds != null) {
                List<TourUser> tourUsers = userService.getAllTourUsersById(userIds, tourId);
                return new ResponseEntity<>(tourUsers, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{tourId}/applications/{userId}")
    public ResponseEntity<Tour> createTourApplication(@PathVariable String tourId, @PathVariable String userId) {
        if (tourService.existsTourById(tourId)) {
            if (userService.existsUserById(userId)) {
                Tour tour = tourService.getTourById(tourId);
                tour.addApplication(userId);
                tourService.saveTour(tour);
                return new ResponseEntity<>(tour, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{tourId}/applications/{userId}")
    public ResponseEntity<Tour> deleteTourApplication(@PathVariable String tourId, @PathVariable String userId) {
        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            if (tour.getApplications().contains(userId)) {
                tour.deleteApplication(userId);
                tourService.saveTour(tour);
            }
            return new ResponseEntity<>(tour, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

