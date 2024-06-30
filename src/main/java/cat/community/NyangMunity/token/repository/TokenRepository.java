package cat.community.NyangMunity.token.repository;

import cat.community.NyangMunity.token.entity.Token;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {

    Optional<Token> findByUserId(Long memberId);
    void deleteByUserId(Long memberId);

}
