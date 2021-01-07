package com.template.image.entities;

import com.template.item.entities.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="url", nullable = false)
    private String url;

    @Column(name="public_id", nullable = false)
    private String publicId;

    @Column(name="thumb", nullable = false)
    private String thumb;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_image_item"))
    private Item item;

    public Image setId(Long id) {
        this.id = id;
        return this;
    }

    public Image setItem(Item item) {
        this.item = item;
        return this;
    }

    public Image setUrl(String url) {
        this.url = url;
        return this;
    }

    public Image setThumb(String thumb) {
        this.thumb = thumb;
        return this;
    }
}
