package cat.community.nyangmunity.member.repository;

import cat.community.nyangmunity.member.entity.Token;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {

    Optional<Token> findByMemberId(Long memberId);
}
