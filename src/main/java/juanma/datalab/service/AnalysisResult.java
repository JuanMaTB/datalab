package juanma.datalab.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 clase simple para agrupar los resultados de un analisis
 se usa como salida comun de los analizadores
*/
@Getter
@AllArgsConstructor
public class AnalysisResult {

    // numero de filas procesadas
    private final int rows;

    // suma total
    private final double sum;

    // media calculada
    private final double avg;

    // valor minimo encontrado
    private final double min;

    // valor maximo encontrado
    private final double max;
}
