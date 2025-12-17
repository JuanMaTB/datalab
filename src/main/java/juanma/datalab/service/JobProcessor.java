package juanma.datalab.service;

import juanma.datalab.domain.*;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 este servicio se encarga de lanzar y coordinar la ejecucion de un job
 aqui se disparan las tasks en paralelo y se espera a que todas terminen
*/

@Service
@RequiredArgsConstructor
public class JobProcessor {

    private final JobRepository jobRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final JobFinalizer jobFinalizer;

    @Transactional
    public void processJob(String jobId) {

        // cargo el job y lo marco como en ejecucion
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(JobStatus.RUNNING);

        // recupero todas las tasks asociadas al job
        List<Task> tasks = taskRepository.findByJobId(jobId);

        // lanzo la ejecucion concurrente de cada task
        List<CompletableFuture<Void>> futures = tasks.stream()
                .map(task ->
                        // cada task se ejecuta de forma asincrona
                        taskService.processTask(task.getId())
                                // si una falla no rompo la espera global
                                .exceptionally(ex -> null)
                )
                .toList();

        // espero a que todas las tasks terminen
        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        // una vez terminadas, calculo el estado final del job
        jobFinalizer.finalizeJob(jobId);
    }
}
