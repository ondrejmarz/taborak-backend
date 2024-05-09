package cz.ondrejmarz.taborakserver.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import cz.ondrejmarz.taborakserver.model.DayPlan;
import cz.ondrejmarz.taborakserver.model.Tour;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DayPlanRepository extends FirestoreReactiveRepository<DayPlan> {

    // Get all existing tours
    Flux<DayPlan> findAll();

    // Get tour by id
    Mono<DayPlan> findById(String id);

    // Save tour
    Mono<DayPlan> save(DayPlan dayPlan);

    // Delete tour
    Mono<Void> delete(DayPlan dayPlan);

    // Return true if tour with id exists
    Mono<Boolean> existsById(String id);
}
