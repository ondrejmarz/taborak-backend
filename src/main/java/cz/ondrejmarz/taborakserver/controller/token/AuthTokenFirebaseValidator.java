package cz.ondrejmarz.taborakserver.controller.token;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * The AuthTokenFirebaseValidator class provides a mechanism for validating Firebase authentication tokens.
 * It verifies the validity of the authentication token using Firebase Authentication service
 * and checks if the user associated with the token has the required roles to access a specific resource.
 */
@Component
public class AuthTokenFirebaseValidator {

    private final UserService userService;

    /**
     * Constructs an instance of AuthTokenFirebaseValidator with the specified UserService dependency.
     *
     * @param userService The UserService instance to be used for retrieving user information.
     */
    @Autowired
    public AuthTokenFirebaseValidator(UserService userService) {
        this.userService = userService;
    }

    /**
     * Validates the provided authentication token against Firebase Authentication service.
     * It also checks if the user associated with the token has the required roles to access a specific resource.
     *
     * @param authToken   The authentication token to be validated.
     * @param tourId      The identifier of the tour/resource for which access is being validated.
     * @param allowedRoles A list of allowed roles for accessing the specified resource.
     * @return True if the token is valid and the user has the required roles; otherwise, false.
     */
    public boolean validateToken(String authToken, String tourId, List<String> allowedRoles) {
        try {
            // Verifies token, if valid then return decoded token, otherwise throws exception
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(authToken);
            // Fetches user from database by id
            User user = userService.getUserById(decodedToken.getUid());
            // Returns true if user has required role in given tour for wanted action
            return allowedRoles.contains(user.getRoles().get(tourId));
        } catch (FirebaseAuthException | NullPointerException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    /**
     * Validates the provided authentication token using Firebase Authentication service.
     * If the token is valid, returns the UID (User ID) extracted from the decoded token.
     *
     * @param authToken The authentication token to be validated.
     * @return The UID (User ID) extracted from the decoded token if the token is valid; otherwise, an empty string.
     */
    public String validateTokenForUID(String authToken) {
        try {
            // Verifies token, if valid then return decoded token, otherwise throws exception
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(authToken);
            // Extracts and returns the UID (User ID) from the decoded token
            return decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return "";
    }
}
