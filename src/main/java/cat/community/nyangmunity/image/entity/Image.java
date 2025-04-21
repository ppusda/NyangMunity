package cat.community.nyangmunity.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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

	@Builder
	public Image(String id, String name, String url, Provider provider) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.provider = provider;
	}
}

