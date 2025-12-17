package juanma.datalab.repository;

import juanma.datalab.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 repositorio jpa para gestionar la entidad job
 se usa para persistencia y consultas basicas
*/
public interface JobRepository extends JpaRepository<Job, String> {}
