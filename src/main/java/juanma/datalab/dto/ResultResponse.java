package juanma.datalab.dto;

public record ResultResponse(
        Long id,
        String jobId,
        int shardIndex,
        String payloadJson
) {}
