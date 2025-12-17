package juanma.datalab.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    // este bean carga las propiedades del executor desde application.yml
    // permite ajustar el pool sin tocar codigo
    @Bean
    @ConfigurationProperties(prefix = "datalab.executor")
    public ExecutorProps executorProps() {
        return new ExecutorProps();
    }

    // este es el executor real que se usara para tareas asincronas
    // define el pool de threads que va a ejecutar trabajo concurrente
    @Bean(name = "datalabExecutor")
    public Executor datalabExecutor(ExecutorProps props) {

        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();

        // numero de threads base que siempre estan vivos
        ex.setCorePoolSize(props.getCorePoolSize());

        // numero maximo de threads en picos de carga
        ex.setMaxPoolSize(props.getMaxPoolSize());

        // cola de tareas pendientes antes de crear nuevos threads
        ex.setQueueCapacity(props.getQueueCapacity());

        // prefijo para identificar facilmente los threads en logs
        ex.setThreadNamePrefix(props.getThreadNamePrefix());

        // inicializo el executor
        ex.initialize();

        return ex;
    }

    // clase interna para agrupar la configuracion del executor
    // se mapea directamente desde el application.yml
    @Data
    public static class ExecutorProps {

        // threads base
        private int corePoolSize = 6;

        // threads maximos
        private int maxPoolSize = 6;

        // tamaño de la cola de tareas
        private int queueCapacity = 200;

        // nombre de los threads
        private String threadNamePrefix = "datalab-";
    }
}
