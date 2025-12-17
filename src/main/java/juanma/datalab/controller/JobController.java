package juanma.datalab.controller;

import jakarta.validation.Valid;
import juanma.datalab.domain.Job;
import juanma.datalab.dto.CreateJobJsonRequest;
import juanma.datalab.dto.JobResponse;
import juanma.datalab.dto.ResultResponse;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

/*
 controlador principal de jobs
 desde aqui se crean, consultan, cancelan y se obtienen resultados
 actua como punto de entrada al flujo concurrente del sistema
*/

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobController {

    // servicio que gestiona la logica de creacion y ejecucion de jobs
    private final JobService jobService;

    // repositorio para lecturas directas del estado del job
    private final JobRepository jobRepository;

    // creacion de un job a partir de un json
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public JobResponse createFromJson(@RequestBody @Valid CreateJobJsonRequest req) {

        // numero de shards por defecto si no se indica
        int shards = (req.shards() == null) ? 6 : req.shards();

        // si no viene paramsJson se construye uno basico
        String paramsJson = (req.paramsJson() == null || req.paramsJson().isBlank())
                ? "{\"sourceType\":\"CSV\",\"source\":\"" + req.sourceUrl() + "\"}"
                : req.paramsJson();

        // creo el job y sus tareas
        String jobId = jobService.createJobAndTasks(paramsJson, shards);

        // lanzo el procesamiento (ejecucion concurrente)
        jobService.processJob(jobId);

        // devuelvo el estado inicial del job
        return toResponse(jobRepository.findById(jobId).orElseThrow());
    }

    // creacion de un job a partir de un fichero csv
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JobResponse createFromFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "shards", required = false) Integer shards,
            @RequestPart(value = "paramsJson", required = false) String paramsJson
    ) throws Exception {

        int realShards = (shards == null) ? 6 : shards;

        // lectura del contenido del fichero
        String csv = new String(file.getBytes(), StandardCharsets.UTF_8);

        // construyo paramsJson si no viene informado
        String realParamsJson = (paramsJson == null || paramsJson.isBlank())
                ? "{\"sourceType\":\"CSV\",\"filename\":\"" + file.getOriginalFilename() + "\",\"bytes\":" + file.getSize() + "}"
                : paramsJson;

        String jobId = jobService.createJobAndTasks(realParamsJson, realShards);
        jobService.processJob(jobId);

        return toResponse(jobRepository.findById(jobId).orElseThrow());
    }

    // obtencion del estado de un job concreto
    @GetMapping("/{id}")
    public JobResponse getJob(@PathVariable String id) {
        Job job = jobRepository.findById(id).orElseThrow();
        return toResponse(job);
    }

    // obtencion paginada de resultados de un job
    @GetMapping("/{id}/results")
    public Page<ResultResponse> getResults(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return jobService.findResults(id, pageable);
    }

    // peticion de cancelacion de un job en curso
    @PostMapping("/{id}:cancel")
    public JobResponse cancel(@PathVariable String id) {
        jobService.cancelJob(id);
        return toResponse(jobRepository.findById(id).orElseThrow());
    }

    // conversion de la entidad job a dto de respuesta
    private JobResponse toResponse(Job job) {

        // calculo del porcentaje de progreso
        double pct = (job.getTotalTasks() == 0)
                ? 0.0
                : (job.getCompletedTasks() * 100.0 / job.getTotalTasks());

        return new JobResponse(
                job.getId(),
                job.getCreatedAt(),
                job.getStatus(),
                job.getTotalTasks(),
                job.getCompletedTasks(),
                job.getFailedTasks(),
                pct,
                job.isCancelRequested()
        );
    }
}
