package cat.community.NyangMunity.board.repository;

import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.board.entity.QBoard;
import cat.community.NyangMunity.board.entity.QBoardLike;
import cat.community.NyangMunity.global.exception.EmptyMaxLikedBoardException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.NoResultException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BoardLikeRepositoryImpl implements BoardLikeRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Board getMaxLikeBoard() {
        QBoard board = QBoard.board;
        QBoardLike boardLike = QBoardLike.boardLike;

        LocalDateTime week = LocalDateTime.now().minusWeeks(1);

        Board maxLikeBoard;

        try {
            maxLikeBoard = jpaQueryFactory
                    .selectFrom(board)
                    .leftJoin(board.boardLikes, boardLike)
                    .where(board.createDate.between(week, LocalDateTime.now()),
                            board.createDate.after(week))
                    .groupBy(board.id)
                    .orderBy(boardLike.id.count().desc())
                    .limit(1)
                    .fetchOne();
        } catch (NoResultException e) {
            throw new EmptyMaxLikedBoardException();
        }

        return maxLikeBoard;
    }
}
