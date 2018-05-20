package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@JsonAutoDetect
public class UserDetailsDto extends DtoObject implements UserDetails {
    private Long id;
    private String name;
    private String email;
    private String remoteCredential;
    private String remoteCredentialSource;
    private Boolean active;

    public UserDetailsDto() {
    }

    public UserDetailsDto(Long id, String name, String email, String remoteCredential, String remoteCredentialSource, Boolean active) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.remoteCredential = remoteCredential;
        this.remoteCredentialSource = remoteCredentialSource;
        this.active = active;
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
