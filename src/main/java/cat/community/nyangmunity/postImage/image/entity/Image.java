package cat.community.nyangmunity.postImage.image.entity;

import java.util.ArrayList;
import java.util.List;

import cat.community.nyangmunity.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

	@Id
	private String id;

	@Column
	private String name;

	@Column
	private String url;

	@Column
	@Enumerated(EnumType.STRING)
	private Provider provider;

	@JoinColumn(name = "member_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@OneToMany(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ImageLike> likes = new ArrayList<>();

	@Builder
	public Image(String id, String name, String url, Provider provider) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.provider = provider;
	}
}

