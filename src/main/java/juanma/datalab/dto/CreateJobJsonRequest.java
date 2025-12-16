package juanma.datalab.dto;

import jakarta.validation.constraints.*;

public record CreateJobJsonRequest(
        @NotBlank String sourceUrl,
        @Min(1) @Max(10_000) Integer shards,
        String paramsJson
) {}
