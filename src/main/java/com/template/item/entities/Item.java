package com.template.item.entities;

import com.template.category.entity.Category;
import com.template.tag.entity.Tag;
import com.template.user.entities.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Indexed
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field
    @Column(name="name", nullable = false)
    @NotNull(message = "Name can not be null")
    private String name;

    @Field
    @Column(name="description")
    private String description;

    @Field
    @Column(name="notes")
    private String notes;

    @Column(name="image", nullable = false)
    @NotNull(message = "Image can not be null")
    private String image;

    @Column(name="link", nullable = false)
    @NotNull(message = "Link can not be null")
    private String link;

    @Field
    @Column(name="approved")
    private boolean isApproved;

    @Column(name = "creation_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_user"))
    private UserEntity user;

    @IndexedEmbedded
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "item_categories",
            joinColumns = {@JoinColumn(name = "item_id",
                    foreignKey = @ForeignKey(name = "fk_item_categories"))},
            inverseJoinColumns = {@JoinColumn(name = "category_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_category_items"))})
    private Set<Category> categories;

    @IndexedEmbedded
    @ManyToMany(fetch = FetchType.LAZY )
    @JoinTable(name = "item_tags",
            joinColumns = {@JoinColumn(name = "item_id",
                    foreignKey = @ForeignKey(name = "fk_item_tags"))},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_tag_items"))})
    private Set<Tag> tags;


}
