package cat.community.NyangMunity.board.repository;

import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.board.request.BoardListRequest;

import java.util.List;

public interface BoardRepositoryCustom {

    List<Board> getList(BoardListRequest boardListRequest);

}
