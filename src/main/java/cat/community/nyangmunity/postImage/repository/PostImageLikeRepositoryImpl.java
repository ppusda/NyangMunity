package cat.community.nyangmunity.postImage.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.postImage.entity.PostImage;
import cat.community.nyangmunity.postImage.entity.QPostImage;
import cat.community.nyangmunity.postImage.entity.QPostImageLike;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostImageLikeRepositoryImpl implements PostImageLikeRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PostImage getMaxLikePostImage() {
		QPostImage postImage = QPostImage.postImage;
		QPostImageLike postImageLike = QPostImageLike.postImageLike;

		LocalDateTime week = LocalDateTime.now().minusWeeks(1);

		PostImage maxLikePost = jpaQueryFactory
			.selectFrom(postImage)
			.leftJoin(postImage.likes, postImageLike)
			.where(postImage.post.createDate.between(week, LocalDateTime.now()),
				postImage.post.createDate.after(week))
			.groupBy(postImage.id)
			.orderBy(postImageLike.id.count().desc())
			.limit(1)
			.fetchOne();

		return Optional.ofNullable(maxLikePost)
			.orElseThrow(() -> new PostNotFoundException("이번 주의 인기 이미지가 없습니다!"));
	}
}
