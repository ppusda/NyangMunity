package cat.community.nyangmunity.post.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.post.entity.Post;
import cat.community.nyangmunity.post.entity.PostImage;
import cat.community.nyangmunity.post.entity.QPost;
import cat.community.nyangmunity.post.entity.QPostImage;
import cat.community.nyangmunity.post.entity.QPostImageLike;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepositoryCustom {

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
