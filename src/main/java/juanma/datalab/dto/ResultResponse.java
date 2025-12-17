package juanma.datalab.dto;

/*
 dto de salida para exponer los resultados de un job
 cada entrada representa el resultado de una task
*/
public record ResultResponse(

        // identificador del resultado
        Long id,

        // id del job al que pertenece
        String jobId,

        // shard que genero este resultado
        int shardIndex,

        // datos del resultado en formato json
        String payloadJson
) {}
