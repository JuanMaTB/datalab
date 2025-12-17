package juanma.datalab.aspects;

// esta excepcion representa errores temporales
// no son fallos definitivos, sino situaciones que pueden resolverse solas
// por ejemplo bloqueos puntuales, concurrencia, o recursos no disponibles
public class TransientDataException extends RuntimeException {

    // constructor simple con mensaje
    // se usa cuando solo quiero indicar el motivo del fallo
    public TransientDataException(String message) {
        super(message);
    }

    // constructor con causa
    // se usa cuando quiero envolver otra excepcion original
    // pero marcandola como transitoria
    public TransientDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
