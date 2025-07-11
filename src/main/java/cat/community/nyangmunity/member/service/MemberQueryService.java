package cat.community.nyangmunity.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.global.exception.member.MemberNotFoundException;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberQueryService {

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public Optional<Member> findMemberByEmail(String email) {
		return memberRepository.findByEmail(email);
	}

	@Transactional(readOnly = true)
	public Optional<Member> findMemberByNickname(String nickname) {
		return memberRepository.findByNickname(nickname);
	}

	@Transactional(readOnly = true)
	public Optional<Member> findMemberByProviderAndProviderId(String provider, String providerId) {
		return memberRepository.findByProviderAndProviderId(provider, providerId);
	}
}
