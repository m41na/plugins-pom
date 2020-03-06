package com.practicaldime.graphql.entity;

import javax.persistence.*;

@Entity
@Table(name = "tbl_reviews")
public class Review {

    @Id
    @GeneratedValue
    @Column(name = "project_id")
    public Long id;
    @ManyToOne
    @JoinColumn(name = "fk_review")
    public Ebook ebook;
    @OneToOne
    @JoinColumn(name = "fk_author")
    public Profile profile;
    @Column(name = "content")
    public String content;
}
