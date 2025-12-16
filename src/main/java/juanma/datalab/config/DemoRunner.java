package juanma.datalab.config;

import juanma.datalab.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DemoRunner implements CommandLineRunner {

    private final JobService jobService;

    @Override
    public void run(String... args) {
        String paramsJson = "{\"sourceType\":\"URL\",\"sourceUrl\":\"mock://dataset/test\"}";
        int shards = 6;

        String jobId = jobService.createJobAndTasks(paramsJson, shards);
        jobService.processJob(jobId);

        System.out.println("JOB creado y procesado: " + jobId);
    }
}
