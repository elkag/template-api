package com.template.category.entity;

import com.template.item.entities.Item;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Indexed
@Table(name = "categories")
public class Category {

    public Category(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @IndexedEmbedded
    @Field(index = Index.YES, store = Store.YES)
    @NaturalId(mutable = true)
    @Column(name = "name", nullable = false, unique = true)
    String name;
}
