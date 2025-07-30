package cat.community.nyangmunity.member.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import cat.community.nyangmunity.global.crypto.ScryptPasswordEncoder;
import cat.community.nyangmunity.global.exception.member.AlreadyExistsMemberException;
import cat.community.nyangmunity.global.exception.member.InvalidLoginRequestException;
import cat.community.nyangmunity.global.exception.member.InvalidPasswordException;
import cat.community.nyangmunity.member.editor.MemberEditor;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.member.request.EditRequest;
import cat.community.nyangmunity.member.request.JoinRequest;
import cat.community.nyangmunity.member.request.LoginRequest;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cat.community.nyangmunity.member.response.GoogleUserResponse;
import cat.community.nyangmunity.member.response.KakaoUserResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberFacadeService {

	private final MemberQueryService memberQueryService;
	private final MemberCommandService memberCommandService;

	private final TokenFacadeService tokenFacadeService;
	private final ScryptPasswordEncoder scryptPasswordEncoder;

	/**
	 * 로그인을 위한 메서드
	 * @param loginRequest 로그인 요청 객체
	 * @throws InvalidLoginRequestException
	 * @return 회원 인증 정보 (회원 정보, 토큰)
	 */
	public MemberAuthenticationResponse login(LoginRequest loginRequest) {
		Member member = memberQueryService.findMemberByEmail(loginRequest.email())
			.orElseThrow(InvalidLoginRequestException::new);

		if (!isPasswordMatches(loginRequest.password(), member)) {
			throw new InvalidLoginRequestException();
		}

		return MemberAuthenticationResponse.builder()
			.memberInfoResponse(MemberInfoResponse.from(member))
			.memberTokens(tokenFacadeService.createTokens(member.getId()))
			.build();
	}

	/**
	 * 소셜 로그인을 위한 메서드
	 * @param provider 로그인 제공 소셜
	 * @param userResponse 소셜 유저 정보
	 * @return 로그인 정보
	 */
	public MemberAuthenticationResponse socialLogin(String provider, Object userResponse) {
		String providerId = "";

		if (userResponse instanceof KakaoUserResponse) {
			providerId = ((KakaoUserResponse) userResponse).id();
		} else if (userResponse instanceof GoogleUserResponse) {
			providerId = ((GoogleUserResponse) userResponse).id();
		}

		Member member = memberQueryService.findMemberByProviderAndProviderId(provider, providerId)
			.orElseGet(() -> joinBySocial(provider, userResponse));

		return MemberAuthenticationResponse.builder()
			.memberInfoResponse(MemberInfoResponse.from(member))
			.memberTokens(tokenFacadeService.createTokens(member.getId()))
			.build();
	}

	private Member joinBySocial(String provider, Object userResponse) {
		Member.MemberBuilder memberBuilder = Member.builder();

		if (userResponse instanceof KakaoUserResponse kakaoUserResponse) {
			memberBuilder
				.email(kakaoUserResponse.kakaoAccount().get("email"))
				.nickname(kakaoUserResponse.kakaoAccount().get("profile"))
				.providerId(kakaoUserResponse.id());
		} else if (userResponse instanceof GoogleUserResponse googleUserResponse) {
			memberBuilder
				.email(googleUserResponse.email())
				.nickname(googleUserResponse.name())
				.providerId(googleUserResponse.id());
		}

		Member member = memberBuilder
			.provider(provider)
			.createDate(LocalDateTime.now())
			.build();

		memberCommandService.saveMember(member);
		return member;
	}


	/**
	 * 로그아웃을 위한 메서드 (토큰 제거)
	 * @param refreshToken 갱신 토큰
	 */
	public void logout(String refreshToken) {
		tokenFacadeService.deleteToken(refreshToken);
	}

	/**
	 * 회원가입을 위한 메서드
	 * @param joinRequest 회원가입 요청 객체
	 * @throws AlreadyExistsMemberException 이미 존재하는 회원 정보 일 때
	 */
	public void join(JoinRequest joinRequest) {
		checkDuplicateEmail(joinRequest.email());
		checkDuplicateNickname(joinRequest.nickname());

		Member member = Member.builder()
			.email(joinRequest.email())
			.password(scryptPasswordEncoder.encrypt(joinRequest.password()))
			.nickname(joinRequest.nickname())
			.createDate(LocalDateTime.now())
			.build();

		memberCommandService.saveMember(member);
	}

	/**
	 * 회원 정보 수정을 위한 메서드
	 * @param editRequest 회원 정보 수정을 위한 요청 객체
	 * @param memberId 수정할 회원 아이디
	 */
	public void edit(EditRequest editRequest, Long memberId) {
		Member member = memberQueryService.findMemberById(memberId);

		if (!isPasswordMatches(editRequest.currentPassword(), member)) {
			throw new InvalidPasswordException();
		}

		// PATCH 방식으로 수정
		MemberEditor.UserEditorBuilder memberEditorBuilder = member.toEditor();
		MemberEditor memberEditor = memberEditorBuilder
			.nickname(
				editRequest.nickname() != null && !editRequest.nickname().isEmpty() ?
					editRequest.nickname() : member.getNickname()
			)
			.password(editRequest.newPassword() != null && !editRequest.newPassword().isEmpty() ?
				scryptPasswordEncoder.encrypt(editRequest.newPassword()) : member.getPassword())
			.build();

		memberCommandService.editMember(member, memberEditor);
	}

	/**
	 * 회원 삭제를 위한 메서드
	 * @param memberId 삭제할 회원 아이디
	 */
	public void cancel(Long memberId) {
		memberCommandService.cancelByMemberId(memberId);
	}

	/**
	 * 회원 프로필 정보를 가져오기 위한 메서드
	 * @param memberId 조회할 회원 아이디
	 * @return 회원 정보 응답 객체
	 */
	public MemberInfoResponse findProfile(Long memberId) {
		return MemberInfoResponse.from(memberQueryService.findMemberById(memberId));
	}

	private void checkDuplicateEmail(String email) {
		if (memberQueryService.findMemberByEmail(email).isPresent())
			throw new AlreadyExistsMemberException();
	}

	private void checkDuplicateNickname(String nickname) {
		if (memberQueryService.findMemberByNickname(nickname).isPresent())
			throw new AlreadyExistsMemberException();
	}

	private boolean isPasswordMatches(String password, Member member) {
		return scryptPasswordEncoder.matches(password, member.getPassword());
	}
}

