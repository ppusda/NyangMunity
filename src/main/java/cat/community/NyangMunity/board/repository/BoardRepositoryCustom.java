package cat.community.NyangMunity.board.repository;

import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.board.request.BoardSearch;

import java.util.List;

public interface BoardRepositoryCustom {

    List<Board> getList(BoardSearch boardSearch);

}
