package juanma.datalab.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean
    @ConfigurationProperties(prefix = "datalab.executor")
    public ExecutorProps executorProps() {
        return new ExecutorProps();
    }

    @Bean(name = "datalabExecutor")
    public Executor datalabExecutor(ExecutorProps props) {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(props.getCorePoolSize());
        ex.setMaxPoolSize(props.getMaxPoolSize());
        ex.setQueueCapacity(props.getQueueCapacity());
        ex.setThreadNamePrefix(props.getThreadNamePrefix());
        ex.initialize();
        return ex;
    }

    @Data
    public static class ExecutorProps {
        private int corePoolSize = 6;
        private int maxPoolSize = 6;
        private int queueCapacity = 200;
        private String threadNamePrefix = "datalab-";
    }
}
