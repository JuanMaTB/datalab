package juanma.datalab.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import juanma.datalab.aspects.RetryableTransient;
import juanma.datalab.aspects.TransientDataException;
import juanma.datalab.domain.Job;
import juanma.datalab.domain.Task;
import juanma.datalab.domain.TaskStatus;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final JobRepository jobRepository;

    private final DatasetService datasetService;
    private final ResultService resultService;
    private final AuditService auditService;
    private final AnalyzerStrategy analyzerStrategy; // 👈 NUEVO
    private final ObjectMapper objectMapper;

    @Async("datalabExecutor")
    @RetryableTransient(maxAttempts = 3, backoffMs = 200)
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
            if (Math.random() < 0.10) {
                throw new TransientDataException("Lectura temporal del dataset falló");
            }

            JsonNode params = objectMapper.readTree(job.getParamsJson());
            String source = params.path("source").asText();

            List<String> lines = datasetService.readAllLines(source);

            int shards = job.getTotalTasks();
            int shard = task.getShardIndex();

            int totalRows = lines.size() - 1;
            int start = 1 + (totalRows * shard) / shards;
            int end = 1 + (totalRows * (shard + 1)) / shards;

            AnalysisResult r = analyzerStrategy.analyze(lines, start, end);

            String payloadJson = """
                    {
                      "jobId": "%s",
                      "shardIndex": %d,
                      "rows": %d,
                      "sumAmount": %.2f,
                      "avgAmount": %.2f,
                      "minAmount": %.2f,
                      "maxAmount": %.2f
                    }
                    """.formatted(
                    job.getId(),
                    shard,
                    r.getRows(),
                    r.getSum(),
                    r.getAvg(),
                    r.getMin(),
                    r.getMax()
            );

            resultService.saveResult(job.getId(), shard, payloadJson);

            task.setStatus(TaskStatus.COMPLETED);
            task.setFinishedAt(LocalDateTime.now());
            taskRepository.save(task);

        } catch (TransientDataException e) {
            throw e;

        } catch (Exception e) {
            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setFinishedAt(LocalDateTime.now());
            taskRepository.save(task);

            auditService.recordTaskFailure(job.getId(), task.getId(), e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }
}
