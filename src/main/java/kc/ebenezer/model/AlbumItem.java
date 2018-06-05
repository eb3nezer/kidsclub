package kc.ebenezer.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "album_items")
@SequenceGenerator(initialValue = 1, name = "itemgen", sequenceName = "album_item_sequence")
public class AlbumItem extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemgen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "order_in_album")
    private Integer order;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "media_descriptor")
    private String mediaDescriptor;

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

    public Integer getOrder() {
        return order;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
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
}
