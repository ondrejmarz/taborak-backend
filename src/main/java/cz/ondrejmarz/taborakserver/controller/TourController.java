package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.service.DayPlanService;
import cz.ondrejmarz.taborakserver.service.GroupService;
import cz.ondrejmarz.taborakserver.service.TourService;
import cz.ondrejmarz.taborakserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller handling operations related to tours.
 * Provides endpoints for retrieving, creating, updating, and deleting tours.
 */
@RestController
@RequestMapping("/tours")
public class TourController {

    private final AuthTokenFirebaseValidator authTokenValidator;

    private final TourService tourService;

    private final UserService userService;

    @Autowired
    public TourController(AuthTokenFirebaseValidator authTokenValidator, TourService tourService, UserService userService) {
        this.authTokenValidator = authTokenValidator;
        this.tourService = tourService;
        this.userService = userService;
    }

    /**
     * Retrieves all tours.
     * @return ResponseEntity containing a list of tours if successful, or an empty list if no tours are found.
     */
    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        List<Tour> tours = tourService.getAllTours();
        return new ResponseEntity<>(tours, HttpStatus.OK);
    }

    /**
     * Retrieves a tour by its ID.
     * @param tourId The ID of the tour to retrieve.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity containing the requested tour if found, or an error response if not authorized or the tour is not found.
     */
    @GetMapping("/{tourId}")
    public ResponseEntity<Tour> getTourById(
            @PathVariable String tourId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major", "minor", "troop", "guest")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            return new ResponseEntity<>(tour, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a new tour and adds user who created it as main authority.
     * @param tour The tour to create.
     * @return ResponseEntity containing the created tour if successful, or an error response if creation fails.
     */
    @PostMapping
    public ResponseEntity<Tour> createTour(
            @RequestBody Tour tour
    ) {
        Tour createdTour = tourService.saveTour(tour);
        try {
            String creatorId = createdTour.getMembers().get(0);
            User creator = userService.getUserById(creatorId);
            creator.addRole(createdTour.getTourId(), "major");
            userService.saveUser(creator);
        } catch (Exception e) {
            return new ResponseEntity<>(createdTour, HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity<>(createdTour, HttpStatus.CREATED);
    }

    /**
     * Updates an existing tour.
     * @param tourId The ID of the tour to update.
     * @param tour The updated tour data.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity containing the updated tour if successful, or an error response if update fails.
     */
    @PutMapping("/{tourId}")
    public ResponseEntity<Tour> updateTour(
            @PathVariable String tourId,
            @RequestBody Tour tour,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            Tour updatedTour = tourService.saveTour(tour);
            return new ResponseEntity<>(updatedTour, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Deletes a tour by its ID.
     * @param tourId The ID of the tour to delete.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity with no content if deletion is successful, or an error response if deletion fails.
     */
    @DeleteMapping("/{tourId}")
    public ResponseEntity<Void> deleteTour(
            @PathVariable String tourId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            tourService.deleteTour(tourService.getTourById(tourId));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

