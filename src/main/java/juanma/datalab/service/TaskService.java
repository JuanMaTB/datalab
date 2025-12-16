package juanma.datalab.service;

import juanma.datalab.domain.*;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final JobRepository jobRepository;

    @Async("datalabExecutor")
    public CompletableFuture<Void> processTask(Long taskId) {

        Task task = taskRepository.findById(taskId).orElseThrow();
        Job job = jobRepository.findById(task.getJob().getId()).orElseThrow();

        if (job.isCancelRequested()) {
            task.setStatus(TaskStatus.CANCELLED);
            task.setFinishedAt(LocalDateTime.now());
            taskRepository.save(task);
            return CompletableFuture.completedFuture(null);
        }

        task.setStatus(TaskStatus.RUNNING);
        task.setStartedAt(LocalDateTime.now());
        task.setAttempts(task.getAttempts() + 1);
        taskRepository.save(task);

        try {
            // Simulación de trabajo
            Thread.sleep(200);

            task.setStatus(TaskStatus.COMPLETED);
            task.setFinishedAt(LocalDateTime.now());
            taskRepository.save(task);

        } catch (Exception e) {
            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setFinishedAt(LocalDateTime.now());
            taskRepository.save(task);
        }

        return CompletableFuture.completedFuture(null);
    }
}
