package ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "users")
@SequenceGenerator(name = "usergen", sequenceName = "user_sequence")
public class User extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usergen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "media_descriptor")
    private String mediaDescriptor;

    @NotNull
    @Column(name = "email")
    private String email;

    @Column(name = "home_phone")
    private String homePhone;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @NotNull
    @Column(name = "logged_in")
    private Boolean loggedIn;

    @NotNull
    @Column(name = "active")
    private Boolean active;

    @Column(name = "remote_credential_source")
    private String remoteCredentialSource;

    @Column(name = "remote_credential")
    private String remoteCredential;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_projects",
            joinColumns = { @JoinColumn(name = "userid") },
            inverseJoinColumns = { @JoinColumn(name = "projectid") }
    )
    private Set<Project> projects = new HashSet<>();

    @OneToMany(mappedBy = "key.user", fetch = FetchType.EAGER)
    private Set<UserSitePermission> userSitePermissions = new HashSet<>();

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    public User() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public User(
            Long id,
            @NotNull String displayName,
            String givenName,
            String familyName,
            @NotNull String email,
            String homePhone,
            String mobilePhone,
            @NotNull Boolean loggedIn,
            @NotNull Boolean active,
            String avatarUrl,
            String mediaDescriptor,
            String remoteCredentialSource,
            String remoteCredential) {
        this();
        this.id = id;
        this.name = displayName;
        this.givenName = givenName;
        this.familyName = familyName;
        this.email = email;
        this.homePhone = homePhone;
        this.mobilePhone = mobilePhone;
        this.loggedIn = loggedIn;
        this.active = active;
        this.avatarUrl = avatarUrl;
        this.mediaDescriptor = mediaDescriptor;
        this.remoteCredentialSource = remoteCredentialSource;
        this.remoteCredential = remoteCredential;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRemoteCredentialSource() {
        return remoteCredentialSource;
    }

    public String getRemoteCredential() {
        return remoteCredential;
    }

    /**
     * Has this user ever logged in?
     * @return Returns true if this user has ever logged in.
     */
    public Boolean getLoggedIn() {
        return loggedIn;
    }

    /**
     * Set whether this user has ever logged in.
     */
    public void setLoggedIn(Boolean value) {
        loggedIn = value;
    }

    public Boolean getActive() {
        return active;
    }

    public void setRemoteCredentialSource(String remoteCredentialSource) {
        this.remoteCredentialSource = remoteCredentialSource;
    }

    public void setRemoteCredential(String remoteCredential) {
        this.remoteCredential = remoteCredential;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<UserSitePermission> getUserSitePermissions() {
        return userSitePermissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated.getTime();
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public static class UserComparator implements Comparator<User> {
        @Override
        public int compare(User o1, User o2) {
            String user1 = getCompareString(o1);
            String user2 = getCompareString(o2);

            return user1.compareTo(user2);
        }

        private String getCompareString(User user) {
            String result = user.getEmail().toLowerCase();

            if (user.getName() != null) {
                result = user.getName().toLowerCase();
            }

            if (user.getFamilyName() != null && !user.getFamilyName().isEmpty()) {
                if (user.getGivenName() != null && !user.getGivenName().isEmpty()) {
                    result = user.getFamilyName().toLowerCase() + " " + user.getGivenName().toLowerCase();
                } else {
                    result = user.getFamilyName().toLowerCase();
                }
            }

            return result;
        }
    }
}
