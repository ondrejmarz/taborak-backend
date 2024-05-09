package cz.ondrejmarz.taborakserver.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import cz.ondrejmarz.taborakserver.model.DayPlan;
import cz.ondrejmarz.taborakserver.model.Group;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface GroupRepository extends FirestoreReactiveRepository<Group> {

    // Get all existing groups
    Flux<Group> findAll();

    // Get group by id
    Mono<Group> findById(String id);

    // Get all existing groups by id
    Flux<Group> findAllById(List<String> groupIds);

    // Save group
    Mono<Group> save(Group group);

    // Delete group
    Mono<Void> delete(Group group);

    // Return true if group with id exists
    Mono<Boolean> existsById(String id);
}
