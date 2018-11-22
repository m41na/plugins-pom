package works.graphql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_chapter")
public class Chapter {

	@Id
	@GeneratedValue
	@Column(name="chapter_id")
	public Long id;
	@Column(name="book_content")
	public String content;
	@ManyToOne()
	@JoinColumn(name = "fk_ebook_id")
	public Ebook ebook;
}
