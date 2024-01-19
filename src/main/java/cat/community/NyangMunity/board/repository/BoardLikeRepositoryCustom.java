package cat.community.NyangMunity.board.repository;
import cat.community.NyangMunity.board.entity.Board;
import com.querydsl.core.Tuple;

import java.util.List;

public interface BoardLikeRepositoryCustom {

    Board getMaxLikeBoard();
}
