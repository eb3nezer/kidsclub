package ebenezer.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "project_properties")
public class ProjectProperty extends ModelObject {
    public static final String STUDENT_MEDIA_PERMITTED_DEFAULT = "studentMediaPermittedDefault";

    @EmbeddedId
    private ProjectPropertyPK key;

    @NonNull
    @Column(name = "property_value")
    private String propertyValue;

    public ProjectProperty() {
    }

    public ProjectProperty(Project project, String propertyKey, String propertyValue) {
        this.key = new ProjectPropertyPK(project, propertyKey);
        this.propertyValue = propertyValue;
    }

    public Project getProject() {
        return key.getProject();
    }

    public String getPropertyKey() {
        return key.getPropertyKey();
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String value) {
        propertyValue = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectProperty that = (ProjectProperty) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Embeddable
    public static class ProjectPropertyPK implements Serializable {
        @NotNull
        @ManyToOne
        @JoinColumn(name = "project_id")
        private Project project;

        @NotNull
        @Column(name = "property_key")
        private String propertyKey;

        public ProjectPropertyPK() {
        }

        public ProjectPropertyPK(@NotNull Project project, @NotNull String propertyKey) {
            this.propertyKey = propertyKey;
            this.project = project;
        }

        public Project getProject() {
            return project;
        }

        public String getPropertyKey() {
            return propertyKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProjectPropertyPK that = (ProjectPropertyPK) o;
            return Objects.equals(getProject(), that.getProject()) &&
                    Objects.equals(getPropertyKey(), that.getPropertyKey());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getProject(), getPropertyKey());
        }
    }
}
