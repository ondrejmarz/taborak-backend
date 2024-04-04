package cz.ondrejmarz.taborakserver.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import cz.ondrejmarz.taborakserver.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface UserRepository extends FirestoreReactiveRepository<User> {

    // Get all existing users
    Flux<User> findAll();

    // Get user by id
    Mono<User> findById(String id);

    // Get all existing users by id
    Flux<User> findAllById(List<String> userIds);

    // Save user
    Mono<User> save(User user);

    // Delete tour
    Mono<Void> delete(User user);

    // Return true if user with id exists
    Mono<Boolean> existsById(String id);
}
