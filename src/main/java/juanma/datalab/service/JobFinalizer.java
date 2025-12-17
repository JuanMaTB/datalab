package juanma.datalab.service;

import juanma.datalab.domain.Job;
import juanma.datalab.domain.JobStatus;
import juanma.datalab.domain.TaskStatus;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 servicio encargado de calcular el estado final de un job
 se ejecuta cuando todas las tasks han terminado o se ha cancelado
*/

@Service
@RequiredArgsConstructor
public class JobFinalizer {

    private final JobRepository jobRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public void finalizeJob(String jobId) {

        // cargo el job actual
        Job job = jobRepository.findById(jobId).orElseThrow();

        // cuento tasks por estado
        long completed = taskRepository.countByJobIdAndStatus(jobId, TaskStatus.COMPLETED);
        long failed = taskRepository.countByJobIdAndStatus(jobId, TaskStatus.FAILED);
        long cancelled = taskRepository.countByJobIdAndStatus(jobId, TaskStatus.CANCELLED);

        // actualizo contadores agregados
        job.setCompletedTasks((int) completed);
        job.setFailedTasks((int) failed);

        // determino el estado final del job
        if (cancelled > 0) {
            job.setStatus(JobStatus.CANCELLED);
        } else if (failed == 0 && completed == job.getTotalTasks()) {
            job.setStatus(JobStatus.COMPLETED);
        } else if (completed > 0 && failed > 0) {
            job.setStatus(JobStatus.PARTIAL_SUCCESS);
        } else {
            job.setStatus(JobStatus.FAILED);
        }

        // persisto el estado final
        jobRepository.save(job);
    }
}
