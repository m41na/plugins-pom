package com.practicaldime.graphql.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class Publication {

	@Id
	@GeneratedValue
	@Column(name="pub_id")
	public Long id;
	@Column(name = "pub_title")
	public String title;
	@Column(name = "publish_date")
	@Temporal(TemporalType.DATE)
	public Date publishDate;
	@Column(name="pub_preface")
	public String preface;
}
