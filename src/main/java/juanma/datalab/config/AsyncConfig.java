package juanma.datalab.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/*
 esta clase define el executor que se usa en todo el proyecto para la ejecucion
 de tareas asincronas y concurrentes

 en lugar de crear threads a mano, se centraliza aqui un thread pool controlado,
 con un numero fijo de hilos y una cola de espera, lo que permite gestionar mejor
 la carga y evitar saturaciones cuando se lanzan muchos jobs o tasks en paralelo

 toda la configuracion del pool se puede ajustar desde application.yml, lo que
 permite cambiar el comportamiento sin tocar codigo y facilita las pruebas
*/

@Configuration
public class AsyncConfig {

    // este bean carga las propiedades del executor desde application.yml
    // actua como contenedor de configuracion
    @Bean
    @ConfigurationProperties(prefix = "datalab.executor")
    public ExecutorProps executorProps() {
        return new ExecutorProps();
    }

    // este es el executor real que usaran los metodos anotados con @Async
    // representa el pool de threads que ejecuta trabajo concurrente
    @Bean(name = "datalabExecutor")
    public Executor datalabExecutor(ExecutorProps props) {

        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();

        // numero de threads base que siempre estan disponibles
        ex.setCorePoolSize(props.getCorePoolSize());

        // numero maximo de threads en picos de carga
        ex.setMaxPoolSize(props.getMaxPoolSize());

        // cola de tareas pendientes antes de crear nuevos threads
        ex.setQueueCapacity(props.getQueueCapacity());

        // prefijo para identificar facilmente los threads en logs
        ex.setThreadNamePrefix(props.getThreadNamePrefix());

        // inicializacion del executor
        ex.initialize();

        return ex;
    }

    // clase interna que agrupa la configuracion del thread pool
    // se rellena automaticamente desde application.yml
    @Data
    public static class ExecutorProps {

        // threads base del pool
        private int corePoolSize = 6;

        // threads maximos permitidos
        private int maxPoolSize = 6;

        // tamaño de la cola de espera
        private int queueCapacity = 200;

        // prefijo de nombre para los threads
        private String threadNamePrefix = "datalab-";
    }
}
