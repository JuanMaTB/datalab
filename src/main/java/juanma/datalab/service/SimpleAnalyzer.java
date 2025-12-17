package juanma.datalab.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 implementacion simple del analizador
 se usa por defecto salvo que se active otro perfil
*/

@Service
@Primary
public class SimpleAnalyzer implements AnalyzerStrategy {

    @Override
    public AnalysisResult analyze(List<String> lines, int start, int end) {

        double sum = 0.0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        int count = 0;

        // proceso el rango asignado al shard
        for (int i = start; i < end; i++) {
            String[] parts = lines.get(i).split(",", -1);
            double amount = Double.parseDouble(parts[4]);

            sum += amount;
            min = Math.min(min, amount);
            max = Math.max(max, amount);
            count++;
        }

        // calculo la media
        double avg = (count == 0) ? 0.0 : sum / count;

        return new AnalysisResult(count, sum, avg, min, max);
    }
}
