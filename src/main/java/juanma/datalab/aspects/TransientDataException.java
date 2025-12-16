package juanma.datalab.aspects;

public class TransientDataException extends RuntimeException {
    public TransientDataException(String message) { super(message); }
    public TransientDataException(String message, Throwable cause) { super(message, cause); }
}
