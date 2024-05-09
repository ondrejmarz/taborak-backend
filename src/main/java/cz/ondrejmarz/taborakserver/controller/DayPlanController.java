package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.DayPlan;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.TourUser;
import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.service.DayPlanService;
import cz.ondrejmarz.taborakserver.service.TourService;
import cz.ondrejmarz.taborakserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/tours/{tourId}/calendar")
public class DayPlanController {

    private final AuthTokenFirebaseValidator authTokenValidator;

    private final TourService tourService;

    private final DayPlanService dayPlanService;

    @Autowired
    public DayPlanController(AuthTokenFirebaseValidator authTokenValidator, TourService tourService, DayPlanService dayPlanService) {
        this.authTokenValidator = authTokenValidator;
        this.tourService = tourService;
        this.dayPlanService = dayPlanService;
    }

    @GetMapping("/{day}")
    public ResponseEntity<DayPlan> getTourDayPlan(
            @PathVariable String tourId,
            @PathVariable String day,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("guest", "major", "minor", "troop")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(day);
            if (tourService.existsTourById(tourId)) {
                List<String> dayPlanIds = tourService.getTourById(tourId).getDailyPrograms();

                for (String dayPlanId: dayPlanIds) {
                    if (dayPlanService.existsDayPlanById(dayPlanId)) {
                        if (dayPlanService.getDayPlanById(dayPlanId).isSameDay(day)) {
                            return new ResponseEntity<>(dayPlanService.getDayPlanById(dayPlanId), HttpStatus.OK);
                        }
                    }
                }
            }
        } catch (ParseException e) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{day}")
    public ResponseEntity<DayPlan> createTourDayPlan(
            @PathVariable String tourId,
            @PathVariable String day,
            @RequestBody DayPlan dayPlan,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major", "minor")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {

            Tour tour = tourService.getTourById(tourId);
            List<String> dayPlanIds = tour.getDailyPrograms();

            // check if day plan is not already created for that day
            for (String dayPlanId: dayPlanIds) {
                if (dayPlanService.existsDayPlanById(dayPlanId)) {
                    if (dayPlanService.getDayPlanById(dayPlanId).isSameDay(day)) {
                        // delete old day plan for the day
                        dayPlanService.deleteDayPlan(dayPlanService.getDayPlanById(dayPlanId));
                        tour.deleteDayPlan(dayPlanId);
                    }
                }
            }

            List<String> exemplaryDayPlansIds = tour.getDailyPrograms();
            dayPlan.setDay(day);
            dayPlan = dayPlanService.fillActivities(dayPlan, dayPlanService.findExemplaryDayPlan(exemplaryDayPlansIds));
            DayPlan createdDayPlan = dayPlanService.saveDayPlan(dayPlan);

            tour.addDayPlan(createdDayPlan.getDayId());
            tourService.saveTour(tour);
            return new ResponseEntity<>(createdDayPlan, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{day}")
    public ResponseEntity<DayPlan> updateTourDayPlan(
            @PathVariable String tourId,
            @PathVariable String day,
            @RequestBody DayPlan dayPlan,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major", "minor")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {

            Tour tour = tourService.getTourById(tourId);
            List<String> dayPlanIds = tour.getDailyPrograms();

            // Check if day plan exists for that day
            for (String dayPlanId: dayPlanIds) {
                if (dayPlanService.existsDayPlanById(dayPlanId)) {
                    if (dayPlanService.getDayPlanById(dayPlanId).isSameDay(day)) {
                        DayPlan updatedDayPlan = dayPlanService.saveDayPlan(dayPlan);
                        return new ResponseEntity<>(updatedDayPlan, HttpStatus.OK);
                    }
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{day}")
    public ResponseEntity<Void> deleteTourDayPlan(
            @PathVariable String tourId,
            @PathVariable String day,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!authTokenValidator.validateToken(authHeader, tourId, List.of("major", "minor")))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (tourService.existsTourById(tourId)) {

            Tour tour = tourService.getTourById(tourId);
            List<String> dayPlanIds = tour.getDailyPrograms();

            // Check if day plan exists for that day
            for (String dayPlanId: dayPlanIds) {
                if (dayPlanService.existsDayPlanById(dayPlanId)) {
                    if (dayPlanService.getDayPlanById(dayPlanId).isSameDay(day)) {
                        tour.deleteDayPlan(dayPlanId);
                        tourService.saveTour(tour);

                        dayPlanService.deleteDayPlan(dayPlanService.getDayPlanById(dayPlanId));

                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    }
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private boolean isDayCorrectFormat(String day, String tourId) {
        try {
            Date targetDay = new SimpleDateFormat("yyyy-MM-dd").parse(day);

            // Check if the target day is within the tour's start and end dates
            Tour tour = tourService.getTourById(tourId);
            if (targetDay.before(tour.getStartDate()) || targetDay.after(tour.getEndDate()))
                return false;

        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}

