package ebenezer.model;

import ebenezer.permissions.SitePermission;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "user_site_permissions")
public class UserSitePermission extends ModelObject {
    @EmbeddedId
    private UserSitePermissionPK key;

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    public UserSitePermission() {
        super();
        key = new UserSitePermissionPK();
        created = System.currentTimeMillis();
        updated = created;
    }

    public UserSitePermission(@NotNull User user, @NotNull SitePermission sitePermission) {
        this();
        this.key = new UserSitePermissionPK(user, sitePermission.toString());
    }

    public User getUser() {
        return key.getUser();
    }

    public SitePermission getPermissionKey() {
        return SitePermission.valueOf(key.getPermissionKey());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSitePermission)) return false;
        UserSitePermission that = (UserSitePermission) o;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getPermissionKey(), that.getPermissionKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getPermissionKey());
    }

    @Embeddable
    public static class UserSitePermissionPK implements Serializable {
        @NotNull
        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @NotNull
        @Column(name = "permission_key")
        private String permissionKey;

        public UserSitePermissionPK() {
        }

        public UserSitePermissionPK(@NotNull User user, @NotNull String permissionKey) {
            this.user = user;
            this.permissionKey = permissionKey;
        }

        public User getUser() {
            return user;
        }

        public String getPermissionKey() {
            return permissionKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserSitePermissionPK)) return false;
            UserSitePermissionPK that = (UserSitePermissionPK) o;
            return Objects.equals(getUser(), that.getUser()) &&
                    Objects.equals(getPermissionKey(), that.getPermissionKey());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getUser(), getPermissionKey());
        }
    }
}
