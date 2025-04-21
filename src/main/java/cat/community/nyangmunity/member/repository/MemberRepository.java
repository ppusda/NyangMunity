package cat.community.nyangmunity.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cat.community.nyangmunity.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	Optional<Member> findByNickname(String nickname);
}

