package cat.community.NyangMunity.board.repository;

import cat.community.NyangMunity.board.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
}
