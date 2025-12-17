package juanma.datalab.domain;

/*
 estados posibles de una task durante su ejecucion
*/
public enum TaskStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}
