package cat.community.nyangmunity.member.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cat.community.nyangmunity.member.editor.MemberEditor;
import cat.community.nyangmunity.member.editor.MemberEditor.UserEditorBuilder;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.ImageLike;
import cat.community.nyangmunity.postImage.post.entity.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Member {

	@Builder
	public Member(String email, String password, String nickname, LocalDateTime createDate, String provider, String providerId) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.createDate = createDate;
		this.provider = provider;
		this.providerId = providerId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column
	private String password;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private LocalDateTime createDate;

	private String provider;

	private String providerId;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Image> images = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Post> posts = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ImageLike> imageLikes = new ArrayList<>();

	public UserEditorBuilder toEditor() {
		return MemberEditor.builder()
			.nickname(nickname)
			.password(password);
	}

	public void edit(MemberEditor memberEditor) {
		nickname = memberEditor.getNickname();
		password = memberEditor.getPassword();
	}
}
