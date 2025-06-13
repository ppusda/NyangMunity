package cat.community.nyangmunity.postImage.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.ImageLike;
import cat.community.nyangmunity.postImage.image.entity.QImage;
import cat.community.nyangmunity.postImage.image.entity.QImageLike;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageLikeRepositoryImpl implements ImageLikeRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Image getMaxLikeImage() {
		QImageLike imageLike = QImageLike.imageLike;
		QImage image = QImage.image;

		LocalDateTime week = LocalDateTime.now().minusWeeks(1);

		ImageLike maxLikeImage = jpaQueryFactory
			.selectFrom(imageLike)
			.where(imageLike.createDate.between(week, LocalDateTime.now()))
			.groupBy(imageLike.image)
			.orderBy(imageLike.id.count().desc())
			.limit(1)
			.fetchOne();

		return Optional.ofNullable(maxLikeImage.getImage())
			.orElseThrow(() -> new PostNotFoundException("이번 주의 인기 이미지가 없습니다!"));
	}
}
