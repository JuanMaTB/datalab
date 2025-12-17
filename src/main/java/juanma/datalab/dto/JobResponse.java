package juanma.datalab.dto;

import juanma.datalab.domain.JobStatus;

import java.time.LocalDateTime;

/*
 dto de salida que representa el estado de un job
 se devuelve al cliente para mostrar progreso y resultado
*/
public record JobResponse(

        // identificador del job
        String id,

        // fecha de creacion
        LocalDateTime createdAt,

        // estado actual del job
        JobStatus status,

        // numero total de tasks
        int totalTasks,

        // tasks completadas
        int completedTasks,

        // tasks fallidas
        int failedTasks,

        // porcentaje de progreso calculado
        double percentCompleted,

        // indica si se ha solicitado la cancelacion
        boolean cancelRequested
) {}
