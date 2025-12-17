package juanma.datalab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/*
 servicio auxiliar para registrar eventos relevantes del sistema
 ahora mismo se usa para auditar fallos de tasks sin interferir
 con las transacciones principales
*/

@Service
@RequiredArgsConstructor
public class AuditService {

    // este metodo se ejecuta siempre en una transaccion nueva
    // asi el registro del fallo no se pierde aunque la task haga rollback
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordTaskFailure(String jobId, Long taskId, String message) {

        // de momento el audit se deja en log
        // mas adelante podria persistirse en una tabla propia
        System.out.println(
                "[AUDIT] " + LocalDateTime.now()
                        + " jobId=" + jobId
                        + " taskId=" + taskId
                        + " error=" + message
        );
    }
}
