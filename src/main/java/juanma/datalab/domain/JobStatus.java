package juanma.datalab.domain;

/*
 estados posibles por los que puede pasar un job
 se usan para reflejar el progreso y el resultado final
*/
public enum JobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    PARTIAL_SUCCESS,
    FAILED,
    CANCELLED
}
