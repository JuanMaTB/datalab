package juanma.datalab.repository;

import juanma.datalab.domain.Result;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Page<Result> findByJobId(String jobId, Pageable pageable);
}
