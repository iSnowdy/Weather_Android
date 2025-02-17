package Exceptions;

public class GeoLocatorException extends RuntimeException {
    public GeoLocatorException(String message) {
        super(message);
    }
    public GeoLocatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
