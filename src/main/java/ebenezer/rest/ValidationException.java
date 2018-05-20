package ebenezer.rest;

import ebenezer.dto.ErrorMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * The user tried to do something for which they have no permission.
 */
public class ValidationException extends WebApplicationException {
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
    public Response getResponse() {
        StringBuilder message = new StringBuilder();
        if (getMessage() != null) {
            message.append(getMessage());
        }

        if (getCause() != null) {
            message.append(" :");
            message.append(getCause().getMessage());
        }

        ErrorMessage errorMessage = new ErrorMessage(message.toString());
        return Response.status(status).entity(errorMessage).build();
    }
}
