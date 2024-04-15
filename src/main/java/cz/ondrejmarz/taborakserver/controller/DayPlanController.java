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
import java.util.Objects;

/**
 * The TourMemberController class handles requests related to tour members.
 * It provides endpoints for retrieving, creating, updating, and deleting tour members,
 * as well as setting and retrieving tour member roles.
 */
@RestController
@RequestMapping("/tours/{tourId}/members")
public class TourMemberController {

    @Autowired
    private AuthTokenFirebaseValidator authTokenValidator;

    @Autowired
    private TourService tourService;

    @Autowired
    private UserService userService;

    /**
     * Retrieves all members of the specified tour.
     *
     * @param tourId     The ID of the tour for which to retrieve members.
     * @param authHeader The authorization token in the request header.
     * @return A ResponseEntity containing a list of TourUser objects representing tour members.
     */
    @GetMapping
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

    /**
     * Retrieves the role of a specific user within the specified tour.
     *
     * @param tourId     The ID of the tour from which to retrieve the user's role.
     * @param userId     The ID of the user whose role is to be retrieved.
     * @param authHeader The authorization token in the request header.
     * @return A ResponseEntity containing the role of the user within the tour.
     */
    @GetMapping("/{userId}/role")
    public ResponseEntity<String> getUserTourRole(
            @PathVariable String tourId,
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateTokenForUID(authHeader).equals(userId))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            if (userService.existsUserById(userId)) {
                String userTourRole =
                        userService.getUserById(userId).getRoles().get(tourId) == null ?
                        userService.getUserById(userId).getRoles().get(tourId) : "null";
                return new ResponseEntity<>(userTourRole, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Adds a user to the specified tour as a member.
     *
     * @param tourId     The ID of the tour to which the user is added as a member.
     * @param userId     The ID of the user to be added as a member.
     * @param authHeader The authorization token in the request header.
     * @return A ResponseEntity containing the updated Tour object after adding the user as a member.
     */
    @PutMapping("/{userId}")
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
     * Sets the role of a user within the specified tour.
     *
     * @param tourId     The ID of the tour in which to set the user's role.
     * @param userId     The ID of the user whose role is to be set.
     * @param role       The role to be assigned to the user within the tour.
     * @param authHeader The authorization token in the request header.
     * @return A ResponseEntity containing the assigned role of the user within the tour.
     */
    @PutMapping("/{userId}/role")
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

    /**
     * Deletes a user from the specified tour. The tour is also deleted if it was its last member.
     *
     * @param tourId     The ID of the tour from which the user is to be deleted.
     * @param userId     The ID of the user to be deleted from the tour.
     * @param authHeader The authorization token in the request header.
     * @return A ResponseEntity containing the updated Tour object after deleting the user from the tour.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Tour> deleteTourMember(
            @PathVariable String tourId,
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            if (tour.getMembers().contains(userId)) {
                tour.deleteMember(userId);

                if (tour.getMembers().isEmpty())
                    tourService.deleteTour(tour);
                else
                    tourService.saveTour(tour);
            }
            return new ResponseEntity<>(tour, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

