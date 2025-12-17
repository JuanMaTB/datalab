package juanma.datalab.aspects;

import java.lang.annotation.*;

/*
 esta anotacion se usa para marcar metodos que pueden fallar de forma transitoria
 por ejemplo accesos a datos, llamadas concurrentes o recursos temporales

 no implementa logica por si sola
 simplemente define la intencion y los parametros
 para que otro aspect (RetryOnTransientErrorAspect) sepa
 que metodos debe interceptar y como debe reintentarlos
*/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetryableTransient {

    // numero maximo de intentos antes de dar el fallo por definitivo
    // se usa cuando el error puede desaparecer al reintentar
    int maxAttempts() default 3;

    // tiempo de espera entre intentos en milisegundos
    // evita reintentos agresivos cuando hay concurrencia
    long backoffMs() default 200;
}
