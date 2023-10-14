package cat.community.NyangMunity.repository;
import com.querydsl.core.Tuple;

import java.util.List;

public interface BoardLikeRepositoryCustom {

    List<Tuple> getMaxLikeBoard();
}
