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

    /*
     cometido de esta clase:
     esta clase se encarga de medir el tiempo de ejecucion de los metodos
     mas importantes del sistema, concretamente los de la capa service y repository

     no modifica la logica de negocio ni la concurrencia existente
     simplemente envuelve la ejecucion para poder ver rendimientos reales
     cuando hay ejecuciones paralelas, asincronas y carga de trabajo
    */

    // logger asociado a esta clase para dejar trazas de rendimiento
    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    // este around intercepta todos los metodos de service y repository
    // aqui es donde se mide el tiempo antes y despues de la ejecucion real
    @Around("execution(* juanma.datalab.service..*(..)) || execution(* juanma.datalab.repository..*(..))")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable {

        // marco el inicio justo antes de ejecutar el metodo real
        long start = System.nanoTime();

        // recupero el traceId del mdc para correlacionar logs por request
        String traceId = MDC.get("traceId");

        try {
            // ejecucion del metodo original
            // el aspect no altera el resultado ni el flujo
            return pjp.proceed();
        } finally {
            // calculo el tiempo total en milisegundos
            long ms = (System.nanoTime() - start) / 1_000_000;

            // log de rendimiento con el nombre del metodo ejecutado
            log.info("Performance {} took {} ms", pjp.getSignature().toShortString(), ms);
        }
    }
}
