package ebenezer.rest;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/rest")
@Component
public class JerseyConfiguration extends ResourceConfig {
    public JerseyConfiguration() {
        register(MultiPartFeature.class);

        register(UserResource.class);
        register(ExceptionMapper.class);
        register(ProjectResource.class);
        register(UserInviteResource.class);
        register(AuditResource.class);
        register(StudentResource.class);
        register(DataResource.class);
        register(AlbumResource.class);
        register(ProjectDocumentResource.class);
        register(AttendanceResource.class);
    }
}
