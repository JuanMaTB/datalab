package juanma.datalab.service;

import juanma.datalab.domain.*;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final TaskRepository taskRepository;
    private final JobProcessor jobProcessor;

    @Transactional
    public String createJobAndTasks(String paramsJson, int shards) {

        String jobId = UUID.randomUUID().toString();

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
        Job job = jobRepository.findById(jobId).orElseThrow();

        job.setCancelRequested(true);
        job.setCancelledAt(LocalDateTime.now());

        if (job.getStatus() == JobStatus.PENDING) {
            job.setStatus(JobStatus.CANCELLED);
        }

        jobRepository.save(job);
    }

    public void processJob(String jobId) {
        jobProcessor.processJob(jobId);
    }
}
