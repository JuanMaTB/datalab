package juanma.datalab.repository;

import juanma.datalab.domain.Result;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 repositorio para acceder a los resultados de un job
 incluye soporte de paginacion
*/
public interface ResultRepository extends JpaRepository<Result, Long> {

    // devuelve los resultados de un job de forma paginada
    Page<Result> findByJobId(String jobId, Pageable pageable);
}
