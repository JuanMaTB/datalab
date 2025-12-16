package juanma.datalab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JobStatus status;

    @Column(nullable = false)
    private int totalTasks;

    @Column(nullable = false)
    private int completedTasks;

    @Column(nullable = false)
    private int failedTasks;

    @Lob
    @Column(nullable = false)
    private String paramsJson;

    @Column(nullable = false)
    private boolean cancelRequested;

    private LocalDateTime cancelledAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Result> results = new ArrayList<>();
}
