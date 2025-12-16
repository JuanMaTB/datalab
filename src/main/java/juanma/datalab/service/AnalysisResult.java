package juanma.datalab.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalysisResult {

    private final int rows;
    private final double sum;
    private final double avg;
    private final double min;
    private final double max;
}
