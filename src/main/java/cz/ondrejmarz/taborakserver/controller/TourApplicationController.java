package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.TourUser;
import cz.ondrejmarz.taborakserver.service.TourService;
import cz.ondrejmarz.taborakserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The TourApplicationController class handles requests related to tour applications.
 * It provides endpoints for retrieving all tour applications, creating a new tour application,
 * and deleting an existing tour application.
 */
@RestController
@RequestMapping("/tours/{tourId}/applications")
public class TourApplicationController {

    @Autowired
    private AuthTokenFirebaseValidator authTokenValidator;

    @Autowired
    private TourService tourService;

    @Autowired
    private UserService userService;

    /**
     * Retrieves all tour applications associated with the specified tour ID.
     *
     * @param tourId     The ID of the tour for which tour applications are to be retrieved.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity containing the list of tour applications if successful, or an HTTP error status.
     */
    @GetMapping
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

    /**
     * Adds a new tour application to the specified tour.
     *
     * @param tourId     The ID of the tour to which the application is added.
     * @param userId     The ID of the user submitting the application.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity containing the updated tour object with the new application if successful, or an HTTP error status.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Tour> createTourApplication(
            @PathVariable String tourId,
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader
    ) {
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

    /**
     * Deletes an existing tour application from the specified tour.
     *
     * @param tourId     The ID of the tour from which the application is deleted.
     * @param userId     The ID of the user whose application is to be deleted.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity containing the updated tour object after deleting the application if successful, or an HTTP error status.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Tour> deleteTourApplication(
            @PathVariable String tourId,
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

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

