package juanma.datalab.repository;

import juanma.datalab.domain.Task;
import juanma.datalab.domain.TaskStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByJobId(String jobId);
    Page<Task> findByJobId(String jobId, Pageable pageable);
    long countByJobIdAndStatus(String jobId, TaskStatus status);
}
