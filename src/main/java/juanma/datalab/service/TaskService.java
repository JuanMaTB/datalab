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
    private final ObjectMapper objectMapper; // Spring lo inyecta

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
            // fallo transitorio para probar retry
            if (Math.random() < 0.10) {
                throw new TransientDataException("Lectura temporal del dataset falló");
            }

            JsonNode params = objectMapper.readTree(job.getParamsJson());
            String source = params.path("source").asText(); // classpath:...

            List<String> lines = datasetService.readAllLines(source);
            if (lines.size() <= 1) {
                throw new IllegalStateException("CSV vacío o sin datos");
            }

            boolean hasHeader = true;
            int dataStart = hasHeader ? 1 : 0;
            int totalRows = lines.size() - dataStart;

            int shards = job.getTotalTasks();
            int shard = task.getShardIndex();

            int start = (totalRows * shard) / shards;
            int end = (totalRows * (shard + 1)) / shards; // exclusivo

            double sum = 0.0;
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            int count = 0;

            for (int i = dataStart + start; i < dataStart + end; i++) {
                String line = lines.get(i);
                String[] parts = line.split(",", -1);

                // columnas: id, customer_id, product, category, amount, timestamp, payment_type, region
                double amount = Double.parseDouble(parts[4]);

                sum += amount;
                min = Math.min(min, amount);
                max = Math.max(max, amount);
                count++;
            }

            double avg = (count == 0) ? 0.0 : sum / count;

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
                    """.formatted(job.getId(), shard, count, sum, avg, min, max);

            resultService.saveResult(job.getId(), shard, payloadJson);

            task.setStatus(TaskStatus.COMPLETED);
            task.setFinishedAt(LocalDateTime.now());
            taskRepository.save(task);

        } catch (TransientDataException e) {
            throw e; // para que el aspect reintente

        } catch (Exception e) {
            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setFinishedAt(LocalDateTime.now());
            taskRepository.save(task);
        }

        return CompletableFuture.completedFuture(null);
    }
}
