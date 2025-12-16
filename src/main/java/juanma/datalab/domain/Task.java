package juanma.datalab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tasks",
        indexes = {
                @Index(name = "idx_tasks_job_id", columnList = "job_id"),
                @Index(name = "idx_tasks_job_shard", columnList = "job_id, shardIndex", unique = true)
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(nullable = false)
    private int shardIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Column(nullable = false)
    private int attempts;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @Column(length = 2000)
    private String errorMessage;
}
