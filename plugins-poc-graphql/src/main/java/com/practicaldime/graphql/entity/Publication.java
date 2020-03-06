package com.practicaldime.graphql.entity;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class Publication {

    @Id
    @GeneratedValue
    @Column(name = "pub_id")
    public Long id;
    @Column(name = "pub_title")
    public String title;
    @Column(name = "publish_date")
    @Temporal(TemporalType.DATE)
    public Date publishDate;
    @Column(name = "pub_preface")
    public String preface;
}
