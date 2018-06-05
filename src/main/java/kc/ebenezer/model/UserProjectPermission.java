package kc.ebenezer.model;

import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.permissions.SitePermission;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "user_project_permissions")
public class UserProjectPermission extends ModelObject {
    @EmbeddedId
    private UserProjectPermissionPK key;

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    public UserProjectPermission() {
        super();
        key = new UserProjectPermissionPK();
        created = System.currentTimeMillis();
        updated = created;
    }

    public UserProjectPermission(User user, Project project, ProjectPermission projectPermission) {
        this();
        this.key = new UserProjectPermissionPK(user, projectPermission.toString(), project);
    }

    public User getUser() {
        return key.getUser();
    }

    public Project getProject() {
        return key.getProject();
    }

    public ProjectPermission getPermissionKey() {
        return ProjectPermission.valueOf(key.getPermissionKey());
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
        if (!(o instanceof UserProjectPermission)) return false;
        UserProjectPermission that = (UserProjectPermission) o;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getPermissionKey(), that.getPermissionKey()) &&
                Objects.equals(getProject(), that.getProject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getPermissionKey(), getProject());
    }

    @Embeddable
    public static class UserProjectPermissionPK implements Serializable {
        @NotNull
        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @NotNull
        @Column(name = "permission_key")
        private String permissionKey;

        @NotNull
        @ManyToOne
        @JoinColumn(name = "project_id")
        private Project project;

        public UserProjectPermissionPK() {
        }

        public UserProjectPermissionPK(@NotNull User user, @NotNull String permissionKey, @NotNull Project project) {
            this.user = user;
            this.permissionKey = permissionKey;
            this.project = project;
        }

        public User getUser() {
            return user;
        }

        public String getPermissionKey() {
            return permissionKey;
        }

        public Project getProject() {
            return project;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserProjectPermissionPK)) return false;
            UserProjectPermissionPK that = (UserProjectPermissionPK) o;
            return Objects.equals(getUser(), that.getUser()) &&
                    Objects.equals(getPermissionKey(), that.getPermissionKey()) &&
                    Objects.equals(getProject(), that.getProject());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getUser(), getPermissionKey(), getProject());
        }
    }
}
