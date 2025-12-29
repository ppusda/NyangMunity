package cat.community.nyangmunity.tag.service;

import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.tag.entity.ImageTag;
import cat.community.nyangmunity.tag.entity.Tag;
import cat.community.nyangmunity.tag.repository.ImageTagRepository;
import cat.community.nyangmunity.tag.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 태그 관련 비즈니스 로직을 처리하는 서비스 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

	private static final int MAX_TAGS_PER_IMAGE = 5;
	private static final int AUTOCOMPLETE_LIMIT = 10;

	private final TagRepository tagRepository;
	private final ImageTagRepository imageTagRepository;

	/**
	 * 이미지에 태그를 추가합니다.
	 *
	 * @param image 태그를 추가할 이미지
	 * @param tagNames 추가할 태그 이름 목록
	 * @throws IllegalArgumentException 태그 개수가 제한을 초과하는 경우
	 */
	@Transactional
	public void addTagsToImage(Image image, List<String> tagNames) {
		if (tagNames == null || tagNames.isEmpty()) {
			return;
		}

		if (tagNames.size() > MAX_TAGS_PER_IMAGE) {
			throw new IllegalArgumentException(
				String.format("태그는 최대 %d개까지만 추가할 수 있습니다.", MAX_TAGS_PER_IMAGE));
		}

		// 중복 제거 및 정규화
		List<String> normalizedTagNames =
			tagNames.stream()
				.map(this::normalizeTagName)
				.distinct()
				.collect(Collectors.toList());

		for (String tagName : normalizedTagNames) {
			Tag tag = tagRepository.findByName(tagName).orElseGet(() -> createNewTag(tagName));

			ImageTag imageTag = ImageTag.builder().image(image).tag(tag).build();

			image.addTag(imageTag);
			imageTagRepository.save(imageTag);
			tag.incrementUsage();
		}
	}

	/**
	 * 이미지에서 태그를 제거합니다.
	 *
	 * @param imageId 이미지 ID
	 */
	@Transactional
	public void removeTagsFromImage(String imageId) {
		List<ImageTag> imageTags = imageTagRepository.findByImageId(imageId);

		for (ImageTag imageTag : imageTags) {
			imageTag.getTag().decrementUsage();
			imageTagRepository.delete(imageTag);
		}
	}

	/**
	 * 새로운 태그를 생성합니다.
	 *
	 * @param name 태그 이름
	 * @return 생성된 태그
	 */
	@Transactional
	public Tag createNewTag(String name) {
		Tag tag = Tag.builder().name(normalizeTagName(name)).build();
		return tagRepository.save(tag);
	}

	/**
	 * 인기 태그 목록을 조회합니다.
	 *
	 * @param limit 조회할 개수
	 * @return 인기 태그 목록
	 */
	public List<Tag> getPopularTags(int limit) {
		return tagRepository.findTopNByOrderByUsageCountDesc(limit);
	}

	/**
	 * 태그 이름으로 자동완성 결과를 조회합니다.
	 *
	 * @param keyword 검색 키워드
	 * @return 매칭되는 태그 목록
	 */
	public List<Tag> autocomplete(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return new ArrayList<>();
		}
		return tagRepository.findByNameContainingOrderByUsageCountDesc(
			normalizeTagName(keyword), Pageable.ofSize(AUTOCOMPLETE_LIMIT));
	}

	/**
	 * 특정 이미지의 태그 목록을 조회합니다.
	 *
	 * @param imageId 이미지 ID
	 * @return 태그 목록
	 */
	public List<Tag> getImageTags(String imageId) {
		return imageTagRepository.findByImageId(imageId).stream()
			.map(ImageTag::getTag)
			.collect(Collectors.toList());
	}

	/**
	 * 태그 이름을 정규화합니다. (소문자 변환, 공백 제거)
	 *
	 * @param tagName 원본 태그 이름
	 * @return 정규화된 태그 이름
	 */
	private String normalizeTagName(String tagName) {
		return tagName.trim().toLowerCase();
	}
}
