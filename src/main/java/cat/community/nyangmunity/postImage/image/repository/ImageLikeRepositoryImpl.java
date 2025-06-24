package cat.community.nyangmunity.postImage.image.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.QImage;
import cat.community.nyangmunity.postImage.image.entity.QImageLike;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageLikeRepositoryImpl implements ImageLikeRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Image getMaxLikeImage() {
		QImage image = QImage.image;
		QImageLike imageLike = QImageLike.imageLike;

		LocalDateTime week = LocalDateTime.now().minusWeeks(1);

		Image maxLikeImage = jpaQueryFactory
			.selectFrom(image)
			.innerJoin(image.likes, imageLike)
			.where(imageLike.createDate.between(week, LocalDateTime.now()))
			.groupBy(imageLike.image)
			.orderBy(imageLike.id.count().desc())
			.limit(1)
			.fetchOne();

		return Optional.ofNullable(maxLikeImage)
			.orElseThrow(() -> new PostNotFoundException("이번 주의 인기 이미지가 없습니다!"));
	}

	public List<String> fetchLikedImageIds(List<String> imageIds, Member member) {
		QImageLike imageLike = QImageLike.imageLike;

		return jpaQueryFactory
			.select(imageLike.image.id)
			.from(imageLike)
			.where(
				imageLike.member.eq(member),
				imageLike.image.id.in(imageIds)
			)
			.fetch();
	}
}
