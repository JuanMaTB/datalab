package juanma.datalab.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/*
 servicio encargado de cargar datasets desde el classpath
 se usa como paso previo al procesamiento concurrente
*/

@Service
public class DatasetService {

    public List<String> readAllLines(String source) {

        // se espera una fuente en formato classpath:...
        if (source == null || !source.startsWith("classpath:")) {
            throw new IllegalArgumentException("Fuente no soportada: " + source);
        }

        String path = source.substring("classpath:".length());

        // lectura completa del fichero en memoria
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource(path).getInputStream(),
                        StandardCharsets.UTF_8
                ))) {
            return br.lines().toList();
        } catch (Exception e) {
            throw new RuntimeException(
                    "No puedo leer el dataset del classpath: " + source,
                    e
            );
        }
    }
}
