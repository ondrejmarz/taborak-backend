package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.Group;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.service.GroupService;
import cz.ondrejmarz.taborakserver.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller handling operations related to groups.
 * Provides endpoints for retrieving, creating, updating, and deleting groups.
 */
@RestController
@RequestMapping("/tours/{tourId}/groups")
public class GroupController {

    private final AuthTokenFirebaseValidator authTokenValidator;

    private final GroupService groupService;

    private final TourService tourService;

    @Autowired
    GroupController(AuthTokenFirebaseValidator authTokenValidator, GroupService groupService, TourService tourService) {
        this.authTokenValidator = authTokenValidator;
        this.groupService = groupService;
        this.tourService = tourService;
    }

    /**
     * Retrieves all groups from specified tour.
     * @return ResponseEntity containing a list of groups if successful, or an empty list if no groups are found.
     */
    @GetMapping
    public ResponseEntity<List<Group>> getAllTourGroups(
            @PathVariable String tourId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major", "minor", "troop", "guest")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            List<String> groupIds = tourService.getTourById(tourId).getGroups();
            List<Group> groups = groupService.getAllByIds(groupIds);
            return new ResponseEntity<>(groups, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieves a group by its ID.
     * @param groupId The ID of the group to retrieve.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity containing the requested group if found, or an error response if not authorized or the group is not found.
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(
            @PathVariable String tourId,
            @PathVariable String groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major", "minor", "troop", "guest")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (groupService.existsGroupById(groupId)) {
            Group group = groupService.getGroupById(groupId);
            return new ResponseEntity<>(group, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Creates all participants with xlsx file.
     * @param tourId The ID of the tour to create participants in.
     * @param xlsxContent The xlsx file content.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity containing the created group if successful, or an error response if update fails.
     */
    @PostMapping("/createAllInXlsx")
    public ResponseEntity<Group> createGroupWithListXlsx(
            @PathVariable String tourId,
            @RequestBody byte[] xlsxContent,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major", "minor")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            List<String> groupIds = tour.getGroups();
            if (!groupIds.isEmpty()) {
                tour.setGroups(Collections.emptyList());
                List<Group> groupsToDelete = groupService.getAllByIds(groupIds);
                for (Group group: groupsToDelete) groupService.deleteGroup(group);
            }
            Group createdGroup = groupService.createGroupWithXlsx(xlsxContent);
            tour.addGroup(createdGroup.getGroupId());
            tourService.saveTour(tour);
            return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Updates an existing group.
     * @param groupId The ID of the group to update.
     * @param group The updated group data.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity containing the updated group if successful, or an error response if update fails.
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<Group> updateGroup(
            @PathVariable String tourId,
            @PathVariable String groupId,
            @RequestBody Group group,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }

    /**
     * Deletes a group by its ID.
     * @param groupId The ID of the group to delete.
     * @param authHeader The authorization token in the request header.
     * @return ResponseEntity with no content if deletion is successful, or an error response if deletion fails.
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable String tourId,
            @PathVariable String groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {
            Tour tour = tourService.getTourById(tourId);
            if (tour.getGroups().contains(groupId)) {
                tour.deleteGroup(groupId);
                tourService.saveTour(tour);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

