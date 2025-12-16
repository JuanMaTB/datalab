package juanma.datalab.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "results",
        indexes = {
                @Index(name = "idx_results_job_id", columnList = "job_id"),
                @Index(name = "idx_results_job_shard", columnList = "job_id, shardIndex", unique = true)
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(nullable = false)
    private int shardIndex;

    @Lob
    @Column(nullable = false)
    private String payloadJson;
}
