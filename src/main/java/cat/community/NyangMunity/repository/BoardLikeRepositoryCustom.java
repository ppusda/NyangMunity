package cat.community.NyangMunity.repository;
import cat.community.NyangMunity.domain.BoardLike;

import java.util.List;

public interface BoardLikeRepositoryCustom {

    List<BoardLike> getMaxLikeBoard();
}
