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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final JobRepository jobRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public JobResponse createFromJson(@RequestBody @Valid CreateJobJsonRequest req) {

        int shards = (req.shards() == null) ? 6 : req.shards();

        String paramsJson = (req.paramsJson() == null || req.paramsJson().isBlank())
                ? "{\"sourceType\":\"CSV\",\"source\":\"" + req.sourceUrl() + "\"}"
                : req.paramsJson();

        String jobId = jobService.createJobAndTasks(paramsJson, shards);
        jobService.processJob(jobId);

        return toResponse(jobRepository.findById(jobId).orElseThrow());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JobResponse createFromFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "shards", required = false) Integer shards,
            @RequestPart(value = "paramsJson", required = false) String paramsJson
    ) throws Exception {

        int realShards = (shards == null) ? 6 : shards;

        String csv = new String(file.getBytes(), StandardCharsets.UTF_8);

        String realParamsJson = (paramsJson == null || paramsJson.isBlank())
                ? "{\"sourceType\":\"CSV\",\"filename\":\"" + file.getOriginalFilename() + "\",\"bytes\":" + file.getSize() + "}"
                : paramsJson;

        String jobId = jobService.createJobAndTasks(realParamsJson, realShards);
        jobService.processJob(jobId);

        return toResponse(jobRepository.findById(jobId).orElseThrow());
    }

    @GetMapping("/{id}")
    public JobResponse getJob(@PathVariable String id) {
        Job job = jobRepository.findById(id).orElseThrow();
        return toResponse(job);
    }

    @GetMapping("/{id}/results")
    public Page<ResultResponse> getResults(@PathVariable String id,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return jobService.findResults(id, pageable);
    }

    @PostMapping("/{id}:cancel")
    public JobResponse cancel(@PathVariable String id) {
        jobService.cancelJob(id);
        return toResponse(jobRepository.findById(id).orElseThrow());
    }

    private JobResponse toResponse(Job job) {
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
