package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto extends DtoObject implements UserDetails {
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
    private ImageCollectionDto imageCollection;

    public UserDto() {
        userSitePermissions = new ArrayList<>();
        loggedIn = Boolean.FALSE;
        active = Boolean.FALSE;
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
        if (this.active == null) {
            this.active = Boolean.FALSE;
        }
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public void setRemoteCredential(String remoteCredential) {
        this.remoteCredential = remoteCredential;
    }

    public void setRemoteCredentialSource(String remoteCredentialSource) {
        this.remoteCredentialSource = remoteCredentialSource;
    }

    public ImageCollectionDto getImageCollection() {
        return imageCollection;
    }

    public void setImageCollection(ImageCollectionDto imageCollection) {
        this.imageCollection = imageCollection;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.EMPTY_SET;
    }

    @Override
    public String getPassword() {
        return "secret";
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
