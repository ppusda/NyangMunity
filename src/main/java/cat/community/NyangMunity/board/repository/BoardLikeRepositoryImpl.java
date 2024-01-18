package cat.community.NyangMunity.board.repository;

import cat.community.NyangMunity.board.entity.QBoardLike;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BoardLikeRepositoryImpl implements BoardLikeRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> getMaxLikeBoard() {
        return jpaQueryFactory.select(QBoardLike.boardLike.board.id, QBoardLike.boardLike.board.id.count())
                .from(QBoardLike.boardLike)
                .groupBy(QBoardLike.boardLike.board.id)
                .orderBy(QBoardLike.boardLike.board.id.count().desc())
                .limit(1L)
                .fetch();
    }
}
