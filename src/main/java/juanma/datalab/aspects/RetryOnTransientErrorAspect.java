package juanma.datalab.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RetryOnTransientErrorAspect {

    private static final Logger log = LoggerFactory.getLogger(RetryOnTransientErrorAspect.class);

    @Around("@annotation(r)")
    public Object around(ProceedingJoinPoint pjp, RetryableTransient r) throws Throwable {
        int attempt = 0;
        Throwable last = null;

        while (attempt < r.maxAttempts()) {
            attempt++;
            try {
                return pjp.proceed();
            } catch (TransientDataException ex) {
                last = ex;

                if (attempt >= r.maxAttempts()) {
                    log.warn("Retry agotado tras {} intentos en {}",
                            attempt, pjp.getSignature().toShortString());
                    throw last;
                }

                long sleep = r.backoffMs() * (1L << (attempt - 1)); // 200, 400, 800...
                log.info("Fallo transitorio (intento {}/{}), reintento en {} ms. Método={}",
                        attempt, r.maxAttempts(), sleep, pjp.getSignature().toShortString());

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            }
        }

        throw last;
    }
}
