package cz.ondrejmarz.taborakserver.service;

import cz.ondrejmarz.taborakserver.model.TourUser;
import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll().collectList().block();
    }

    public List<User> getAllUsersById(List<String> userIds) {
        return userRepository.findAllById(userIds).collectList().block();
    }

    public List<TourUser> getAllTourUsersById(List<String> userIds, String tourId) {
        List<User> foundUsers = userRepository.findAllById(userIds).collectList().block();
        if (foundUsers != null)
            return foundUsers.stream().map(
                    user -> {
                        String role = user.getRoles().getOrDefault(tourId, "null");
                        return new TourUser(user.getUserId(), user.getUserName(), user.getEmail(), role );
                    } ).collect(Collectors.toList());
        return Collections.emptyList();
    }

    public User getUserById(String id) {
        return userRepository.findById(id).block();
    }

    public User saveUser(User user) {
        return userRepository.save(user).block();
    }

    public void deleteUser(User user) {
        userRepository.delete(user).block();
    }

    public Boolean existsUserById(String id) {
        return userRepository.existsById(id).block();
    }
}
