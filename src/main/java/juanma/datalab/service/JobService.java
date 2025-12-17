package juanma.datalab.service;

import juanma.datalab.domain.Job;
import juanma.datalab.domain.JobStatus;
import juanma.datalab.domain.Task;
import juanma.datalab.domain.TaskStatus;
import juanma.datalab.dto.ResultResponse;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.repository.ResultRepository;
import juanma.datalab.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 este service concentra la gestion del ciclo de vida de un job
 aqui se crean jobs y tasks, se delega la ejecucion concurrente
 y se exponen consultas de resultados
*/

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final TaskRepository taskRepository;
    private final ResultRepository resultRepository;
    private final JobProcessor jobProcessor;

    @Transactional
    public String createJobAndTasks(String paramsJson, int shards) {

        // genero el id del job
        String jobId = UUID.randomUUID().toString();

        // creo el job en estado pendiente
        Job job = Job.builder()
                .id(jobId)
                .createdAt(LocalDateTime.now())
                .status(JobStatus.PENDING)
                .totalTasks(shards)
                .completedTasks(0)
                .failedTasks(0)
                .paramsJson(paramsJson)
                .cancelRequested(false)
                .build();

        jobRepository.save(job);

        // creo las tasks asociadas, una por shard
        for (int i = 0; i < shards; i++) {
            Task task = Task.builder()
                    .job(job)
                    .shardIndex(i)
                    .status(TaskStatus.PENDING)
                    .attempts(0)
                    .build();
            taskRepository.save(task);
        }

        return jobId;
    }

    @Transactional
    public void cancelJob(String jobId) {

        // marco el job como cancelado
        Job job = jobRepository.findById(jobId).orElseThrow();

        job.setCancelRequested(true);
        job.setCancelledAt(LocalDateTime.now());

        // si aun no habia empezado, se cancela directamente
        if (job.getStatus() == JobStatus.PENDING) {
            job.setStatus(JobStatus.CANCELLED);
        }

        jobRepository.save(job);
    }

    // delego la ejecucion del job en el processor
    public void processJob(String jobId) {
        jobProcessor.processJob(jobId);
    }

    @Transactional(readOnly = true)
    public Page<ResultResponse> findResults(String jobId, Pageable pageable) {

        // obtengo los resultados paginados y los convierto a dto
        return resultRepository.findByJobId(jobId, pageable)
                .map(r -> new ResultResponse(
                        r.getId(),
                        jobId,
                        r.getShardIndex(),
                        r.getPayloadJson()
                ));
    }
}
