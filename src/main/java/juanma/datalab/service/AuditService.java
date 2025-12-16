package juanma.datalab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    // De momento lo dejamos simple (log). Si quieres, luego lo persistimos en tabla AUDIT_LOG.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordTaskFailure(String jobId, Long taskId, String message) {
        System.out.println(
                "[AUDIT] " + LocalDateTime.now()
                        + " jobId=" + jobId
                        + " taskId=" + taskId
                        + " error=" + message
        );
    }
}
