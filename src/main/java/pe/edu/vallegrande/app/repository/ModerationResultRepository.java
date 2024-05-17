package pe.edu.vallegrande.app.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux; //List
//import reactor.core.publisher.Mono; //Individual
import pe.edu.vallegrande.app.entity.ModerationResult;

@Repository
public interface ModerationResultRepository extends ReactiveCrudRepository<ModerationResult, Integer> {

	@Query("SELECT * FROM moderacion WHERE status = 'A'")
    Flux<ModerationResult> findByActiveTrue();

}