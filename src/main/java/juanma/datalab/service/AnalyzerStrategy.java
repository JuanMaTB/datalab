package juanma.datalab.service;

import java.util.List;

/*
 interfaz comun para los distintos tipos de analisis
 permite cambiar la implementacion sin tocar el flujo principal
*/
public interface AnalyzerStrategy {

    // analiza un rango concreto de lineas (shard)
    AnalysisResult analyze(List<String> lines, int start, int end);

}
