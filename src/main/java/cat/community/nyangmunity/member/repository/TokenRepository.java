package cat.community.nyangmunity.member.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import cat.community.nyangmunity.member.entity.Token;

@Repository
public interface TokenRepository extends CrudRepository<Token, String> {

	Optional<Token> findByMemberId(Long memberId);
}
