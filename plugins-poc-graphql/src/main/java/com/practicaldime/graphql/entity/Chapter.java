package com.practicaldime.graphql.entity;

import javax.persistence.*;

@Entity
@Table(name = "tbl_chapter")
public class Chapter {

    @Id
    @GeneratedValue
    @Column(name = "chapter_id")
    public Long id;
    @Column(name = "book_content")
    public String content;
    @ManyToOne()
    @JoinColumn(name = "fk_ebook_id")
    public Ebook ebook;
}
