package juanma.datalab.dto;

import jakarta.validation.constraints.*;

/*
 dto de entrada para crear un job a partir de json
 valida los parametros basicos antes de entrar en el service
*/
public record CreateJobJsonRequest(

        // origen del dataset o recurso a procesar
        @NotBlank
        String sourceUrl,

        // numero de shards para dividir el trabajo
        // se limita para evitar abusos
        @Min(1)
        @Max(10_000)
        Integer shards,

        // parametros adicionales en formato json
        String paramsJson
) {}
