package kc.ebenezer.rest;

import javax.ws.rs.core.Response;

/**
 * The user tried to do something for which they have no permission.
 */
public class ValidationException extends KCException {
    private static final Response.Status status = Response.Status.PRECONDITION_FAILED;

    public ValidationException() {
        super(status);
    }

    public ValidationException(String message) {
        super(message, status);
    }

    public ValidationException(Throwable cause) {
        super(cause, status);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause, status);
    }

    @Override
    protected Response.Status getStatus() {
        return status;
    }
}
