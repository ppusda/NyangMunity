package cat.community.NyangMunity.repository;

import cat.community.NyangMunity.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByUserId(Long userId);
    @Transactional
    Optional<Token> deleteByUserId(Long userId);
}
