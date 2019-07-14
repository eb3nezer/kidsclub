package kc.ebenezer.rest;

import kc.ebenezer.dto.ErrorMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public abstract class KCException extends WebApplicationException {
    public KCException(Response.Status status) {
        super(status);
    }

    public KCException(String message, Response.Status status) {
        super(message, status);
    }

    public KCException(Throwable cause, Response.Status status) {
        super(cause, status);
    }

    public KCException(String message, Throwable cause, Response.Status status) {
        super(message, cause, status);
    }

    abstract protected Response.Status getStatus();

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
        return Response.status(getStatus()).entity(errorMessage).build();
    }
}
