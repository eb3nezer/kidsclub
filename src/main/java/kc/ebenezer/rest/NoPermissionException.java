package kc.ebenezer.rest;

import javax.ws.rs.core.Response;

/**
 * The user tried to do something for which they have no permission.
 */
public class NoPermissionException extends KCException {
    private static final Response.Status status = Response.Status.FORBIDDEN;

    public NoPermissionException() {
        super(status);
    }

    public NoPermissionException(String message) {
        super(message, status);
    }

    public NoPermissionException(Throwable cause) {
        super(cause, status);
    }

    public NoPermissionException(String message, Throwable cause) {
        super(message, cause, status);
    }

    @Override
    protected Response.Status getStatus() {
        return status;
    }
}
