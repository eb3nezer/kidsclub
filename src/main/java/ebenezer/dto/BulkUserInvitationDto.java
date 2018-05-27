package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class BulkUserInvitationDto {
    private String emails;
    private Long projectId;

    public BulkUserInvitationDto() {
    }

    public BulkUserInvitationDto(String emails, Long projectId) {
        this.projectId = projectId;
        this.emails = emails;
    }

    public String getEmails() {
        return emails;
    }

    public Long getProjectId() {
        return projectId;
    }
}
