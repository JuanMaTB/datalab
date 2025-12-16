package juanma.datalab.service;

import java.util.List;

public interface AnalyzerStrategy {

    AnalysisResult analyze(List<String> lines, int start, int end);

}
