package pl.pateman.wiredi.exception;

public class WireNameClassResolveException extends DIException {
    public WireNameClassResolveException(String message) {
        super(message);
    }

    public WireNameClassResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public WireNameClassResolveException(Throwable cause) {
        super(cause);
    }
}
