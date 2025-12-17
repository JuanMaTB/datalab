package juanma.datalab.domain;

import jakarta.persistence.*;
import lombok.*;

/*
 esta entidad representa el resultado generado por una tarea concreta
 cada resultado pertenece a un job y a un shard especifico
*/

@Entity
@Table(
        name = "results",
        indexes = {
                // indice para buscar rapido todos los resultados de un job
                @Index(name = "idx_results_job_id", columnList = "job_id"),

                // garantiza un unico resultado por job y shard
                @Index(name = "idx_results_job_shard", columnList = "job_id, shardIndex", unique = true)
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {

    // identificador interno del resultado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // job al que pertenece este resultado
    // se carga de forma lazy para no penalizar rendimiento
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // indice del shard que ha generado este resultado
    @Column(nullable = false)
    private int shardIndex;

    // datos del resultado en formato json
    @Lob
    @Column(nullable = false)
    private String payloadJson;
}
