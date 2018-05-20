package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInvitationDto {
    private String email;
    private String name;
    private Long projectId;

    public UserInvitationDto() {
    }

    public UserInvitationDto(String email, String name, Long projectId) {
        this.email = email;
        this.projectId = projectId;
        if (name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            this.name = email;
        }
    }

    public String getEmail() {
        return email;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return email;
        }
    }
}
