package juanma.datalab.service;

import juanma.datalab.domain.*;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobFinalizer {

    private final JobRepository jobRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public void finalizeJob(String jobId) {

        Job job = jobRepository.findById(jobId).orElseThrow();

        long completed = taskRepository.countByJobIdAndStatus(jobId, TaskStatus.COMPLETED);
        long failed = taskRepository.countByJobIdAndStatus(jobId, TaskStatus.FAILED);
        long cancelled = taskRepository.countByJobIdAndStatus(jobId, TaskStatus.CANCELLED);

        job.setCompletedTasks((int) completed);
        job.setFailedTasks((int) failed);

        if (cancelled > 0) {
            job.setStatus(JobStatus.CANCELLED);
        } else if (failed == 0 && completed == job.getTotalTasks()) {
            job.setStatus(JobStatus.COMPLETED);
        } else if (completed > 0 && failed > 0) {
            job.setStatus(JobStatus.PARTIAL_SUCCESS);
        } else {
            job.setStatus(JobStatus.FAILED);
        }
    }
}
