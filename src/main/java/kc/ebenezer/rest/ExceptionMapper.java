package kc.ebenezer.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Throwable> {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("error", exception);
        String errorMessage = "Error: " + exception.getMessage();
        return Response.status(500)
                .entity(errorMessage)
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
