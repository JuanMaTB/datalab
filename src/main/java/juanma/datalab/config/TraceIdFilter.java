package juanma.datalab.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/*
 esta clase actua como filtro de entrada para todas las requests http

 aqui se genera (o se reutiliza si ya viene informado) un trace id unico
 que se guarda en el mdc para poder seguir una request completa en los logs

 esto es especialmente importante en este proyecto porque hay ejecucion
 concurrente y asincrona, y sin un identificador comun seria muy dificil
 seguir que logs pertenecen al mismo job o a la misma peticion

 el filtro se ejecuta al inicio de la request y limpia el mdc al final
 para evitar que el contexto se mezcle entre threads o peticiones distintas
*/

@Component
public class TraceIdFilter implements Filter {

    // clave usada en el mdc para identificar la request
    public static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // si la request es http intento leer un trace id entrante
            String incoming = (request instanceof HttpServletRequest req)
                    ? req.getHeader("X-Trace-Id")
                    : null;

            // si no viene ninguno, genero uno nuevo
            String traceId = (incoming != null && !incoming.isBlank())
                    ? incoming
                    : UUID.randomUUID().toString();

            // guardo el trace id en el mdc
            // todos los logs del mismo thread lo heredaran
            MDC.put(TRACE_ID, traceId);

            // continuo con la cadena normal de filtros
            chain.doFilter(request, response);
        } finally {
            // limpio el mdc para no contaminar otras requests
            MDC.remove(TRACE_ID);
        }
    }
}
