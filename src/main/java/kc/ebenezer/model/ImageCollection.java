package kc.ebenezer.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "images")
@SequenceGenerator(name = "imagecolgen", sequenceName = "image_col_sequence")
public class ImageCollection extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "imagecolgen")
    @Column(name = "id")
    private Long id;

    @NonNull
    @OneToMany(mappedBy = "imageCollection", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Image> images;

    @NonNull
    @Column(name = "created")
    private Long created;

    @NonNull
    @Column(name = "updated")
    private Long updated;

    public ImageCollection() {
        created = System.currentTimeMillis();
        updated = created;
        images = new HashSet<>();
    }

    public void updated() {
        updated = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public Date getUpdated() {
        return new Date(updated);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageCollection)) return false;
        ImageCollection that = (ImageCollection) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
