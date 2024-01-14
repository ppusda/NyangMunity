package cat.community.NyangMunity.board.repository;

import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.board.entity.QBoard;
import cat.community.NyangMunity.board.request.BoardListRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Board> getList(BoardListRequest boardListRequest) {
        return jpaQueryFactory.selectFrom(QBoard.board)
                .limit(boardListRequest.getSize())
                .offset(boardListRequest.getOffset())
                .orderBy(QBoard.board.id.desc()) // 정렬 쿼리 추가
                .fetch();
    }
}
