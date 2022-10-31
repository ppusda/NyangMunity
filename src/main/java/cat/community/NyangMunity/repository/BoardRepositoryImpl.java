package cat.community.NyangMunity.repository;

import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.QBoard;
import cat.community.NyangMunity.request.BoardSearch;
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
