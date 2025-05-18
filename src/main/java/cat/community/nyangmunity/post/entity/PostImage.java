package cat.community.nyangmunity.post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cat.community.nyangmunity.image.entity.Image;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "image_id")
	private Image image;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "post_id")
	private Post post;

	@Column
	private LocalDateTime createDate;

	@OneToMany(mappedBy = "postImage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<PostImageLike> likes = new ArrayList<>();

	public PostImage(Image image) {
		this.image = image;
		this.createDate = LocalDateTime.now();
	}

	public void setRelation(Post post) {
		this.post = post;
	}
}
