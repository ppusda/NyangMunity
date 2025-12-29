package cat.community.nyangmunity.postImage.image.entity;

import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.tag.entity.ImageTag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "image",
    indexes = {
        @Index(name = "idx_upload_date", columnList = "upload_date DESC"),
        @Index(name = "idx_likes_count", columnList = "likes_count DESC"),
        @Index(name = "idx_views_count", columnList = "views_count DESC")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

	@Id
	private String id;

	@Column
	private String name;

	@Column
	private String url;

	@Column(length = 500)
	private String description;

	@Column
	@Enumerated(EnumType.STRING)
	private Provider provider;

	@Column(name = "upload_date", nullable = false)
	private LocalDateTime uploadDate;

	@Column(name = "likes_count", nullable = false)
	private Integer likesCount;

	@Column(name = "views_count", nullable = false)
	private Integer viewsCount;

	@Column(name = "expires_at")
	private LocalDateTime expiresAt;

	@JoinColumn(name = "member_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@OneToMany(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ImageLike> likes = new ArrayList<>();

	@OneToMany(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ImageTag> imageTags = new ArrayList<>();

	@Builder
	public Image(String id, String name, String url, String description, Provider provider, Member member) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.description = description;
		this.provider = provider;
		this.member = member;
		this.likesCount = 0;
		this.viewsCount = 0;
		this.uploadDate = LocalDateTime.now();
		this.expiresAt = calculateExpiresAt();
	}

	@PrePersist
	protected void onCreate() {
		if (uploadDate == null) {
			uploadDate = LocalDateTime.now();
		}
		if (likesCount == null) {
			likesCount = 0;
		}
		if (viewsCount == null) {
			viewsCount = 0;
		}
		if (expiresAt == null) {
			expiresAt = calculateExpiresAt();
		}
	}

	/**
	 * 이미지에 태그를 추가합니다.
	 */
	public void addTag(ImageTag imageTag) {
		imageTags.add(imageTag);
	}

	/**
	 * 좋아요 수를 증가시킵니다.
	 */
	public void incrementLikes() {
		this.likesCount++;
		updateExpiresAt();
	}

	/**
	 * 좋아요 수를 감소시킵니다.
	 */
	public void decrementLikes() {
		if (this.likesCount > 0) {
			this.likesCount--;
			updateExpiresAt();
		}
	}

	/**
	 * 조회수를 증가시킵니다.
	 */
	public void incrementViews() {
		this.viewsCount++;
	}

	/**
	 * 만료 시간을 계산합니다.
	 * - 기본: 30일
	 * - 좋아요 10개 이상: 90일
	 * - 좋아요 50개 이상: 영구 보관 (null)
	 */
	private LocalDateTime calculateExpiresAt() {
		if (likesCount >= 50) {
			return null; // 영구 보관
		} else if (likesCount >= 10) {
			return uploadDate.plusDays(90);
		} else {
			return uploadDate.plusDays(30);
		}
	}

	/**
	 * 좋아요 수 변경 시 만료 시간을 업데이트합니다.
	 */
	private void updateExpiresAt() {
		this.expiresAt = calculateExpiresAt();
	}

	/**
	 * 설명을 업데이트합니다.
	 */
	public void updateDescription(String description) {
		this.description = description;
	}

	/**
	 * 업로더(회원)를 설정합니다.
	 */
	public void setMember(Member member) {
		this.member = member;
	}
}
