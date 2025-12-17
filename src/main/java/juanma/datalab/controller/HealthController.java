//Controlador de salud simple
package juanma.datalab.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
 controlador tecnico para comprobar que el servicio esta levantado
 devuelve una respuesta simple sin pasar por logica de negocio
*/

@RestController
public class HealthController {

    // endpoint basico de salud
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "OK",
                "service", "DataLab"
        );
    }
}
