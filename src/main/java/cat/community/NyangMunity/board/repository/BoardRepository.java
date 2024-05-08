package cat.community.NyangMunity.board.repository;

import cat.community.NyangMunity.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAllByOrderByCreateDateDesc(Pageable pageable);
}
