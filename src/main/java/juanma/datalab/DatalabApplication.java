package juanma.datalab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/*
 clase principal de arranque de la aplicacion
 aqui se habilita la ejecucion asincrona del proyecto
*/

@SpringBootApplication
@EnableAsync
public class DatalabApplication {

    // el main desde el que se levanta todo el contexto de spring
    public static void main(String[] args) {
        SpringApplication.run(DatalabApplication.class, args);
    }
}
