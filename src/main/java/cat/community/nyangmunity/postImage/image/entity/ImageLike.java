package cat.community.nyangmunity.postImage.image.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import cat.community.nyangmunity.member.entity.Member;
import jakarta.persistence.Column;
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
public class ImageLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "image_id")
	private Image image;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createDate;

	@Builder
	public ImageLike(Image image, Member member) {
		this.image = image;
		this.member = member;
	}
}
