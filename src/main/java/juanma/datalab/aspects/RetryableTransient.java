package juanma.datalab.aspects;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetryableTransient {
    int maxAttempts() default 3;
    long backoffMs() default 200;
}
