package com.template.tag.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Indexed
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @IndexedEmbedded
    @Field(index = Index.YES, store = Store.YES)
    @NaturalId
    @Column(name = "name", nullable = false, unique = true)
    String name;

    public Tag(String name) {
        this.name = name;
    }
}
