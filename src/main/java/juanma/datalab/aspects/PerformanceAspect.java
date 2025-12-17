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
public class PerformanceAspect {

    // el logger de la clase, lo uso para dejar trazas de rendimiento
    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    // este around intercepta todos los metodos del paquete service y repository
    // la idea es medir cuanto tarda cada llamada sin tocar el codigo de negocio
    @Around("execution(* juanma.datalab.service..*(..)) || execution(* juanma.datalab.repository..*(..))")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable {

        // marco el inicio justo antes de ejecutar el metodo real
        long start = System.nanoTime();

        // recupero el traceId del mdc para poder correlacionar logs
        // aunque aqui no lo uso directamente, queda preparado para ampliaciones
        String traceId = MDC.get("traceId");

        try {
            // aqui se ejecuta el metodo original (el service o el repository)
            return pjp.proceed();
        } finally {
            // calculo el tiempo total en milisegundos
            long ms = (System.nanoTime() - start) / 1_000_000;

            // dejo un log con el nombre corto del metodo y lo que ha tardado
            // esto me permite ver facilmente cuellos de botella
            log.info("Performance {} took {} ms", pjp.getSignature().toShortString(), ms);
        }
    }
}
