package juanma.datalab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
 esta entidad representa una unidad de trabajo individual
 cada job se divide en varias tasks que pueden ejecutarse en paralelo
*/

@Entity
@Table(
        name = "tasks",
        indexes = {
                // indice para localizar rapido las tasks de un job
                @Index(name = "idx_tasks_job_id", columnList = "job_id"),

                // asegura una unica task por job y shard
                @Index(name = "idx_tasks_job_shard", columnList = "job_id, shardIndex", unique = true)
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    // identificador interno de la task
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // job al que pertenece esta task
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // indice del shard que identifica esta unidad de trabajo
    @Column(nullable = false)
    private int shardIndex;

    // estado actual de la task
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    // numero de intentos realizados (retry)
    @Column(nullable = false)
    private int attempts;

    // momento de inicio de la ejecucion
    private LocalDateTime startedAt;

    // momento de finalizacion
    private LocalDateTime finishedAt;

    // mensaje de error en caso de fallo
    @Column(length = 2000)
    private String errorMessage;
}
