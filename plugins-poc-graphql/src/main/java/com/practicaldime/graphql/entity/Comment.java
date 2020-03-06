package com.practicaldime.graphql.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_comment")
public class Comment {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    public Long id;
    @Column(name = "comment_text")
    public String content;
    @Column(name = "comment_date")
    @Temporal(TemporalType.DATE)
    public Date commentDate;
    @ManyToOne()
    @JoinColumn(name = "fk_author_id")
    public Profile author;
    @ManyToOne()
    @JoinColumn(name = "fk_parent_blog")
    public Blog parentBlog;
    @ManyToOne()
    @JoinColumn(name = "fk_parent_comment")
    public Comment parentComment;
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "fk_comment_remarks",
            joinColumns = {@JoinColumn(name = "parent_comment")},
            inverseJoinColumns = {@JoinColumn(name = "remark_comment")})
    public Set<Comment> remarks = new HashSet<>();
}
