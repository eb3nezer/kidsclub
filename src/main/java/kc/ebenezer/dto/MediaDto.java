package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaDto extends DtoObject {
    private String descriptor;
    private UserDto owner;
    private String contentType;
    private String description;

    public MediaDto() {
    }

    public MediaDto(String descriptor, UserDto owner, String contentType, String description) {
        this.descriptor = descriptor;
        this.owner = owner;
        this.contentType = contentType;
        this.description = description;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public UserDto getOwner() {
        return owner;
    }

    public String getContentType() {
        return contentType;
    }

    public String getDescription() {
        return description;
    }
}
