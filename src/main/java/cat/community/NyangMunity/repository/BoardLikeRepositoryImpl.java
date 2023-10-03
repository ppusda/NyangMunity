package cat.community.NyangMunity.repository;

import cat.community.NyangMunity.domain.BoardLike;
import cat.community.NyangMunity.domain.QBoardLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BoardLikeRepositoryImpl implements BoardLikeRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BoardLike> getMaxLikeBoard() {
        return jpaQueryFactory.selectFrom(QBoardLike.boardLike)
                .groupBy(QBoardLike.boardLike.board.id)
                .orderBy(QBoardLike.boardLike.board.id.count().desc())
                .limit(1L)
                .fetch();
    }
}
