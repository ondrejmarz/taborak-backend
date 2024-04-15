package cz.ondrejmarz.taborakserver.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import cz.ondrejmarz.taborakserver.model.Tour;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TourRepository extends FirestoreReactiveRepository<Tour> {

    // Get all existing tours
    Flux<Tour> findAll();

    // Get tour by id
    Mono<Tour> findById(String id);

    // Save tour
    Mono<Tour> save(Tour tour);

    // Delete tour
    Mono<Void> delete(Tour tour);

    // Return true if tour with id exists
    Mono<Boolean> existsById(String id);
}
