package kc.ebenezer.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "album_items")
@SequenceGenerator(initialValue = 1, name = "itemgen", sequenceName = "album_item_sequence")
public class AlbumItem extends ModelObject implements PhotoUploadable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemgen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "order_in_album")
    private Integer order;

    @Column(name = "description")
    private String description;

    @Column(name = "media_descriptor")
    private String mediaDescriptor;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "images_id")
    private ImageCollection imageCollection;

    @NotNull
    @Column(name = "created")
    private Long created;

    @NotNull
    @Column(name = "updated")
    private Long updated;

    public AlbumItem() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public AlbumItem(@NotNull Integer order, @NotNull String mediaDescriptor, String description) {
        this();
        this.order = order;
        this.mediaDescriptor = mediaDescriptor;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    @Override
    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public Date getUpdated() {
        return new Date(updated);
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void updated() {
        updated = System.currentTimeMillis();
    }

    @Override
    public ImageCollection getImageCollection() {
        return imageCollection;
    }

    @Override
    public void setImageCollection(ImageCollection imageCollection) {
        this.imageCollection = imageCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlbumItem albumItem = (AlbumItem) o;
        return Objects.equals(getId(), albumItem.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }
}
