package kc.ebenezer.model;

import javax.persistence.*;

@Entity
@Table(name = "sysconfig")
public class SystemConfiguration extends ModelObject {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "version")
    private Integer version;

    public SystemConfiguration() {
        id = 1L;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
