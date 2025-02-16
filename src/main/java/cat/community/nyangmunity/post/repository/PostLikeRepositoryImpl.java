package cat.community.nyangmunity.post.repository;

import cat.community.nyangmunity.post.entity.Post;
import cat.community.nyangmunity.post.entity.QPost;
import cat.community.nyangmunity.post.entity.QPostLike;
import cat.community.nyangmunity.global.exception.EmptyMaxLikedBoardException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.NoResultException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Post getMaxLikePost() {
        QPost post = QPost.post;
        QPostLike postLike = QPostLike.postLike;

        LocalDateTime week = LocalDateTime.now().minusWeeks(1);

        Post maxLikePost;

        try {
            maxLikePost = jpaQueryFactory
                    .selectFrom(post)
                    .leftJoin(post.likes, postLike)
                    .where(post.createDate.between(week, LocalDateTime.now()),
                            post.createDate.after(week))
                    .groupBy(post.id)
                    .orderBy(postLike.id.count().desc())
                    .limit(1)
                    .fetchOne();
        } catch (NoResultException e) {
            throw new EmptyMaxLikedBoardException();
        }

        return maxLikePost;
    }
}
