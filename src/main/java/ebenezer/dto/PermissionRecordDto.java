package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionRecordDto {
    private String key;
    private String description;
    private Boolean granted;

    public PermissionRecordDto() {
    }

    public PermissionRecordDto(String key, String description, Boolean granted) {
        this.key = key;
        this.description = description;
        this.granted = granted;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getGranted() {
        return granted;
    }

    public Boolean isGranted() {
        return granted;
    }
}
