package cat.community.NyangMunity.repository;
import cat.community.NyangMunity.domain.BoardLike;

import java.util.List;
import java.util.Optional;

public interface BoardLikeRepositoryCustom {

    List<BoardLike> getMaxLikeBoard();
}
