package cuchaz.m3l.api.versioning;

/**
 * @author Caellian
 */
public class InvalidVersionFormatException extends Exception {
    public InvalidVersionFormatException() {
    }

    public InvalidVersionFormatException(String message) {
        super(message);
    }

    public InvalidVersionFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVersionFormatException(Throwable cause) {
        super(cause);
    }

    public InvalidVersionFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
