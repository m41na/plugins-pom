package com.practicaldime.graphql.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_blog")
public class Blog extends Publication {

    @Column(name = "blog_content")
    public String content;
    @ElementCollection
    public List<String> tags = new ArrayList<>();
    @ManyToOne()
    @JoinColumn(name = "fk_author_id")
    public Profile author;
    @OneToMany(mappedBy = "parentBlog")
    public Set<Comment> comments = new HashSet<>();
    @ManyToOne()
    @JoinColumn(name = "fk_parent_blog")
    public Blog parentBlog;
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "fk_blog_series",
            joinColumns = {@JoinColumn(name = "parent_blog")},
            inverseJoinColumns = {@JoinColumn(name = "series_blog")})
    public Set<Blog> series = new HashSet<>();
}
