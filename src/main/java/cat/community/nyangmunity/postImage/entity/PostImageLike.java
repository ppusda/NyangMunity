package cat.community.nyangmunity.postImage.entity;

import cat.community.nyangmunity.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostImageLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "post_image_id")
	private PostImage postImage;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public PostImageLike(PostImage postImage, Member member) {
		this.postImage = postImage;
		this.member = member;
	}
}
