package cat.community.NyangMunity.repository;

import cat.community.NyangMunity.domain.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    Optional<BoardLike> findByBoardIdAndUserId(Long bid, Long uid);

    @Transactional
    Optional<BoardLike> deleteByBoardIdAndUserId(Long bid, Long uid);
}
