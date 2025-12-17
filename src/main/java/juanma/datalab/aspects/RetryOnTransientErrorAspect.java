package juanma.datalab.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RetryOnTransientErrorAspect {

    // logger del aspect, aqui dejo trazas de reintentos y fallos
    private static final Logger log = LoggerFactory.getLogger(RetryOnTransientErrorAspect.class);

    // este around solo se aplica a metodos anotados con @RetryableTransient
    // no intercepta nada mas del sistema
    @Around("@annotation(r)")
    public Object around(ProceedingJoinPoint pjp, RetryableTransient r) throws Throwable {

        // contador de intentos
        int attempt = 0;

        // guardo la ultima excepcion por si hay que relanzarla
        Throwable last = null;

        // bucle de reintentos controlado por la anotacion
        while (attempt < r.maxAttempts()) {
            attempt++;
            try {
                // ejecuto el metodo real
                // si todo va bien, salgo directamente
                return pjp.proceed();
            } catch (TransientDataException ex) {
                // solo capturo errores transitorios
                last = ex;

                // si ya he agotado los intentos, dejo traza y relanzo el error
                if (attempt >= r.maxAttempts()) {
                    log.warn(
                            "Retry agotado tras {} intentos en {}",
                            attempt,
                            pjp.getSignature().toShortString()
                    );
                    throw last;
                }

                // calculo el backoff exponencial
                // ejemplo: 200, 400, 800...
                long sleep = r.backoffMs() * (1L << (attempt - 1));

                // dejo log informativo del reintento
                log.info(
                        "Fallo transitorio (intento {}/{}), reintento en {} ms. Metodo={}",
                        attempt,
                        r.maxAttempts(),
                        sleep,
                        pjp.getSignature().toShortString()
                );

                try {
                    // pausa antes de reintentar
                    Thread.sleep(sleep);
                } catch (InterruptedException ie) {
                    // si el thread se interrumpe, respeto la interrupcion
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            }
        }

        // nunca deberia llegar aqui, pero por seguridad relanzo la ultima excepcion
        throw last;
    }
}
