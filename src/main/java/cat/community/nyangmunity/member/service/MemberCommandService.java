package cat.community.nyangmunity.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.member.editor.MemberEditor;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

	private final MemberRepository memberRepository;

	@Transactional
	public void saveMember(Member member) {
		memberRepository.save(member);
	}

	@Transactional
	public void cancelByMemberId(Long memberId) {
		memberRepository.deleteById(memberId);
	}

	@Transactional
	public void editMember(Member member, MemberEditor memberEditor) {
		member.edit(memberEditor);
	}
}
