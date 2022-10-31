package cat.community.NyangMunity.repository;

import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.request.BoardSearch;

import java.util.List;

public interface BoardRepositoryCustom {

    List<Board> getList(BoardSearch boardSearch);

}
