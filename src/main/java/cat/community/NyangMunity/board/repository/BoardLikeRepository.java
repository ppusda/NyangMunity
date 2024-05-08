package cat.community.NyangMunity.board.repository;

import cat.community.NyangMunity.board.entity.BoardLike;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long>,
        BoardLikeRepositoryCustom {

    Optional<BoardLike> findByBoardIdAndUserId(Long bid, Long uid);

    @Transactional
    void deleteByBoardIdAndUserId(Long bid, Long uid);

}
