package cat.community.nyangmunity.tag.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 태그 엔티티
 *
 * <p>이미지에 부여할 수 있는 태그를 나타냅니다.
 * 태그는 중복되지 않으며, 사용 횟수를 추적합니다.
 */
@Entity
@Table(
	name = "tag",
	indexes = {
		@Index(name = "idx_tag_name", columnList = "name"),
		@Index(name = "idx_tag_usage_count", columnList = "usageCount DESC")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, length = 50)
	private String name;

	@Column(nullable = false)
	private Integer usageCount;

	@Builder
	public Tag(String name) {
		this.name = name;
		this.usageCount = 0;
	}

	/**
	 * 태그 사용 횟수를 증가시킵니다.
	 */
	public void incrementUsage() {
		this.usageCount++;
	}

	/**
	 * 태그 사용 횟수를 감소시킵니다.
	 */
	public void decrementUsage() {
		if (this.usageCount > 0) {
			this.usageCount--;
		}
	}
}
