package cat.community.NyangMunity.repository;

import cat.community.NyangMunity.domain.Session;
import cat.community.NyangMunity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByAccessToken(String AccessToken);
}
