package cat.community.nyangmunity.post.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.post.entity.Post;
import cat.community.nyangmunity.post.entity.QPost;
import cat.community.nyangmunity.post.entity.QPostLike;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Post getMaxLikePost() {
        QPost post = QPost.post;
        QPostLike postLike = QPostLike.postLike;

        LocalDateTime week = LocalDateTime.now().minusWeeks(1);

        Post maxLikePost = jpaQueryFactory
            .selectFrom(post)
            .leftJoin(post.likes, postLike)
            .where(post.createDate.between(week, LocalDateTime.now()),
                    post.createDate.after(week))
            .groupBy(post.id)
            .orderBy(postLike.id.count().desc())
            .limit(1)
            .fetchOne();

        return Optional.ofNullable(maxLikePost)
            .orElseThrow(() -> new PostNotFoundException("이번 주의 인기 이미지가 없습니다!"));
    }
}
