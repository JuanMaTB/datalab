package juanma.datalab.service;

import juanma.datalab.domain.*;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class JobProcessor {

    private final JobRepository jobRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final JobFinalizer jobFinalizer;

    @Transactional
    public void processJob(String jobId) {

        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(JobStatus.RUNNING);

        List<Task> tasks = taskRepository.findByJobId(jobId);

        List<CompletableFuture<?>> futures = tasks.stream()
                .map(task ->
                        taskService.processTask(task.getId())
                                .exceptionally(ex -> null) // evita romper el allOf
                )
                .toList();

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        jobFinalizer.finalizeJob(jobId);
    }
}
