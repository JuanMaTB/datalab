package juanma.datalab.repository;

import juanma.datalab.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {}
