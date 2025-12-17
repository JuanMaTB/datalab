package juanma.datalab.repository;

import juanma.datalab.domain.Task;
import juanma.datalab.domain.TaskStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 repositorio para acceder y consultar las tasks de un job
 se usa para seguimiento de estado y progreso
*/
public interface TaskRepository extends JpaRepository<Task, Long> {

    // devuelve todas las tasks de un job
    List<Task> findByJobId(String jobId);

    // devuelve las tasks de un job de forma paginada
    Page<Task> findByJobId(String jobId, Pageable pageable);

    // cuenta cuantas tasks de un job estan en un estado concreto
    long countByJobIdAndStatus(String jobId, TaskStatus status);
}
