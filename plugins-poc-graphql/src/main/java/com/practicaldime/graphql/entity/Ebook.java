package com.practicaldime.graphql.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_ebook")
public class Ebook extends Publication {

    @OneToMany(mappedBy = "ebook")
    public Set<Review> reviews = new HashSet<>();
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "tbl_ebook_author",
            joinColumns = {@JoinColumn(name = "author_id")},
            inverseJoinColumns = {@JoinColumn(name = "pub_id")})
    public Set<Profile> authors = new HashSet<>();
    @OneToMany(mappedBy = "ebook")
    public Set<Chapter> chapters = new HashSet<>();
}
