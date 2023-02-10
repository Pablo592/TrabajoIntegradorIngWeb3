package ar.edu.iua.iw3.negocio.excepciones;

public class BadRequest extends Exception {

    public BadRequest() {}

    public BadRequest(String message) {
        super(message);
    }

    public BadRequest(Throwable cause) {
        super(cause);
    }

    public BadRequest(String message, Throwable cause) {
        super(message, cause);
    }

    protected BadRequest(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
