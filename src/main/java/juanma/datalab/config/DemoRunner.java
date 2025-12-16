package juanma.datalab.config;

/*
 * DemoRunner
 * ==========
 * Runner de prueba
 *
 * Se usó para verificar:
 *  - Creación de Jobs y Tasks
 *  - Ejecución concurrente con @Async + ThreadPoolTaskExecutor
 *  - Funcionamiento del AOP de métricas y retry
 *  - Persistencia en H2
 *
 * Está comentado para evitar ejecución automática al arrancar el servidor.
 * Si se quiere volver a usar:
 *  - Descomentar la anotación @Component
 *  - Descomentar el método run(...)
 */

// @Component
// @Profile("dev")
// @RequiredArgsConstructor
public class DemoRunner /* implements CommandLineRunner */ {

    // private final JobService jobService;

    /*
    @Override
    public void run(String... args) {

        String paramsJson = "{\"sourceType\":\"URL\",\"sourceUrl\":\"mock://dataset/test\"}";
        int shards = 6;

        String jobId = jobService.createJobAndTasks(paramsJson, shards);
        jobService.processJob(jobId);

        System.out.println("JOB creado y procesado (demo): " + jobId);
    }
    */
}
