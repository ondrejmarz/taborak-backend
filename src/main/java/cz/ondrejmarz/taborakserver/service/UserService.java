package cz.ondrejmarz.taborakserver.service;

import cz.ondrejmarz.taborakserver.model.User;
import cz.ondrejmarz.taborakserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsersById(List<String> userIds) {
        return userRepository.findAllById(userIds).collectList().block();
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
