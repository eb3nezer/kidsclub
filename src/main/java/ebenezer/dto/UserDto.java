package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect
public class UserDto extends DtoObject {
    private Long id;
    private String name;
    private String givenName;
    private String familyName;
    private String email;
    private String homePhone;
    private String mobilePhone;
    private Boolean loggedIn;
    private Boolean active;
    private String avatarUrl;
    private String mediaDescriptor;
    private String remoteCredential;
    private String remoteCredentialSource;
    private List<String> userSitePermissions;

    public UserDto() {
    }

    public UserDto(Long id) {
        this.id = id;
    }

    public UserDto(
            Long id,
            String name,
            String givenName,
            String familyName,
            String email,
            String homePhone,
            String mobildPhone,
            Boolean loggedIn,
            Boolean active,
            String avatarUrl,
            String mediaDescriptor,
            String remoteCredential,
            String remoteCredentialSource,
            List<String> userSitePermissions) {
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.id = id;
        this.email = email;
        this.homePhone = homePhone;
        this.mobilePhone = mobildPhone;
        this.loggedIn = loggedIn;
        this.active = active;
        this.avatarUrl = avatarUrl;
        this.mediaDescriptor = mediaDescriptor;
        this.remoteCredential = remoteCredential;
        this.remoteCredentialSource = remoteCredentialSource;
        this.userSitePermissions = userSitePermissions;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRemoteCredential() {
        return remoteCredential;
    }

    public String getRemoteCredentialSource() {
        return remoteCredentialSource;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public Boolean getActive() {
        return active;
    }

    public List<String> getUserSitePermissions() {
        return userSitePermissions;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }
}
