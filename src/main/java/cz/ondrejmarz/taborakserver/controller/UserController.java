package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
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

@RestController
@RequestMapping("/users")
public class UserController {

    private final AuthTokenFirebaseValidator authTokenValidator;

    private final UserService userService;

    @Autowired
    public UserController(AuthTokenFirebaseValidator authTokenValidator, UserService userService) {
        this.authTokenValidator = authTokenValidator;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsersById(
            @RequestParam List<String> userIds,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (Objects.equals(authTokenValidator.validateTokenForUID(authHeader), ""))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<User> users = userService.getAllUsersById(userIds);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> saveUser(
            @RequestBody User user,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (Objects.equals(authTokenValidator.validateTokenForUID(authHeader), ""))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (!userService.existsUserById(user.getUserId())) {
            User newUser = userService.saveUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}

