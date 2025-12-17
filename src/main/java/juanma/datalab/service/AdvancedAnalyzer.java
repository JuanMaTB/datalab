package juanma.datalab.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 implementacion de analisis pensada para entorno prod
 simula un procesamiento mas costoso por cada shard
*/

@Service
@Profile("prod")
public class AdvancedAnalyzer implements AnalyzerStrategy {

    @Override
    public AnalysisResult analyze(List<String> lines, int start, int end) {

        // simulacion de carga para representar un analisis pesado
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}

        double sum = 0.0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        int count = 0;

        // proceso solo el rango asignado a este shard
        for (int i = start; i < end; i++) {
            String[] parts = lines.get(i).split(",", -1);
            double amount = Double.parseDouble(parts[4]);

            sum += amount;
            min = Math.min(min, amount);
            max = Math.max(max, amount);
            count++;
        }

        // calculo del promedio
        double avg = (count == 0) ? 0.0 : sum / count;

        // devuelvo el resultado parcial del shard
        return new AnalysisResult(count, sum, avg, min, max);
    }
}
