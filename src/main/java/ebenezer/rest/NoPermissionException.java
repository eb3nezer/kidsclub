package ebenezer.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * The user tried to do something for which they have no permission.
 */
public class NoPermissionException extends WebApplicationException {
    public NoPermissionException() {
        super(Response.Status.FORBIDDEN);
    }

    public NoPermissionException(String message) {
        super(message, Response.Status.FORBIDDEN);
    }

    public NoPermissionException(Throwable cause) {
        super(cause, Response.Status.FORBIDDEN);
    }

    public NoPermissionException(String message, Throwable cause) {
        super(message, cause, Response.Status.FORBIDDEN);
    }
}
