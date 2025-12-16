package juanma.datalab.dto;

import juanma.datalab.domain.JobStatus;

import java.time.LocalDateTime;

public record JobResponse(
        String id,
        LocalDateTime createdAt,
        JobStatus status,
        int totalTasks,
        int completedTasks,
        int failedTasks,
        double percentCompleted,
        boolean cancelRequested
) {}
