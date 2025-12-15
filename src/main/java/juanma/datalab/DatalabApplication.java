package juanma.datalab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DatalabApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatalabApplication.class, args);
    }
}
