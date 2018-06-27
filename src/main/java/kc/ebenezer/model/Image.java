package kc.ebenezer.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "image")
@SequenceGenerator(name = "imagegen", sequenceName = "image_sequence")
public class Image extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "imagegen")
    @Column(name = "id")
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "collection_id")
    private ImageCollection imageCollection;

    @NonNull
    @Column(name = "size")
    private String size;

    @NonNull
    @Column(name = "media_descriptor")
    private String mediaDescriptor;

    @NonNull
    @Column(name = "created")
    private Long created;

    @NonNull
    @Column(name = "updated")
    private Long updated;

    public Image() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public Image(ImageCollection imageCollection, ImageSize size, String mediaDescriptor) {
        this();
        this.imageCollection = imageCollection;
        this.size = size.getCode();
        this.mediaDescriptor = mediaDescriptor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImageCollection getImageCollection() {
        return imageCollection;
    }

    public void setImageCollection(ImageCollection imageCollection) {
        this.imageCollection = imageCollection;
    }

    public ImageSize getSize() {
        return ImageSize.getForCode(size);
    }

    public void setSize(ImageSize size) {
        this.size = size.getCode();
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public void setCreated(Date created) {
        this.created = created.getTime();
    }

    public Date getUpdated() {
        return new Date(updated);
    }

    public void setUpdated(Date updated) {
        this.updated = updated.getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image)) return false;
        Image image = (Image) o;
        return Objects.equals(getId(), image.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }
}
