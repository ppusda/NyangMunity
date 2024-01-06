package cat.community.NyangMunity.board.repository;

import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.domain.QBoard;
import cat.community.NyangMunity.board.request.BoardSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Board> getList(BoardSearch boardSearch) {
        return jpaQueryFactory.selectFrom(QBoard.board)
                .limit(boardSearch.getSize())
                .offset(boardSearch.getOffset())
                .orderBy(QBoard.board.id.desc()) // 정렬 쿼리 추가
                .fetch();
    }
}
