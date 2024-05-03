package cz.ondrejmarz.taborakserver.service;

import cz.ondrejmarz.taborakserver.model.TourUser;
import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import cz.ondrejmarz.taborakserver.repository.TourRepository;
import cz.ondrejmarz.taborakserver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll() {
        List<User> userList = Arrays.asList(
                new User("ID1", "Name1", "Email1", new HashMap<>()),
                new User("ID2", "Name2", "Email2", new HashMap<>())
        );

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(userList));

        List<User> result = userService.getAll();

        assertNotNull(result);
        assertEquals(userList, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsersById() {
        List<String> userIds = Arrays.asList("ID1", "ID2");
        List<User> userList = Arrays.asList(
                new User("ID1", "Name1", "Email1", new HashMap<>()),
                new User("ID2", "Name2", "Email2", new HashMap<>())
        );

        when(userRepository.findAllById(userIds)).thenReturn(Flux.fromIterable(userList));

        List<User> result = userService.getAllUsersById(userIds);

        assertNotNull(result);
        assertEquals(userList, result);
        verify(userRepository, times(1)).findAllById(userIds);
    }

    @Test
    void getAllTourUsersById() {
        String tourId = "tourID";
        List<String> userIds = Arrays.asList("ID1", "ID2");
        List<User> userList = Arrays.asList(
                new User("ID1", "Name1", "Email1", Collections.singletonMap(tourId, "Role1")),
                new User("ID2", "Name2", "Email2", Collections.singletonMap(tourId, "Role2"))
        );
        List<TourUser> expected = Arrays.asList(
                new TourUser("ID1", "Name1", "Email1", "Role1"),
                new TourUser("ID2", "Name2", "Email2", "Role2")
        );

        when(userRepository.findAllById(userIds)).thenReturn(Flux.fromIterable(userList));

        List<TourUser> result = userService.getAllTourUsersById(userIds, tourId);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(userRepository, times(1)).findAllById(userIds);
    }

    @Test
    void getUserById() {
        String userId = "ID1";
        User user = new User("ID1", "Name1", "Email1", new HashMap<>());

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void saveUser() {
        User user = new User("ID1", "Name1", "Email1", new HashMap<>());

        when(userRepository.save(user)).thenReturn(Mono.just(user));

        User result = userService.saveUser(user);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUser() {
        User user = new User("ID1", "Name1", "Email1", new HashMap<>());

        when(userRepository.delete(user)).thenReturn(Mono.empty());

        userService.deleteUser(user);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void existsUserById() {
        String userId = "ID1";

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));

        boolean result = userService.existsUserById(userId);
        verify(userRepository, times(1)).existsById(userId);

        assertTrue(result);
    }
}