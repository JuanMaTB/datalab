package juanma.datalab.service;

import juanma.datalab.domain.Job;
import juanma.datalab.domain.Result;
import juanma.datalab.repository.JobRepository;
import juanma.datalab.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/*
 servicio encargado de persistir resultados de forma aislada
 cada resultado se guarda en su propia transaccion
*/

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final JobRepository jobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveResult(String jobId, int shardIndex, String payloadJson) {

        // referencia al job sin cargarlo completo
        Job jobRef = jobRepository.getReferenceById(jobId);

        // construyo el resultado del shard
        Result r = Result.builder()
                .job(jobRef)
                .shardIndex(shardIndex)
                .payloadJson(payloadJson)
                .build();

        // persisto el resultado
        resultRepository.save(r);
    }
}
