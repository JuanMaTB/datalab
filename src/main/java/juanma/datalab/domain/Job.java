package juanma.datalab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 esta entidad representa un job completo dentro del sistema
 aqui se guarda su estado general, parametros, contadores y relaciones
 sirve como punto central para coordinar tareas, resultados y cancelacion
*/

@Entity
@Table(name = "jobs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    // identificador del job
    @Id
    @Column(length = 36)
    private String id;

    // momento de creacion del job
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // estado actual del job (creado, en proceso, terminado, cancelado, etc)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JobStatus status;

    // numero total de tareas asociadas al job
    @Column(nullable = false)
    private int totalTasks;

    // tareas completadas correctamente
    @Column(nullable = false)
    private int completedTasks;

    // tareas que han fallado
    @Column(nullable = false)
    private int failedTasks;

    // parametros originales del job en formato json
    @Lob
    @Column(nullable = false)
    private String paramsJson;

    // indica si se ha solicitado la cancelacion del job
    @Column(nullable = false)
    private boolean cancelRequested;

    // momento en el que se solicito la cancelacion
    private LocalDateTime cancelledAt;

    // tareas que pertenecen a este job
    // se gestionan en cascada junto al job
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    // resultados generados por las tareas del job
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Result> results = new ArrayList<>();
}
