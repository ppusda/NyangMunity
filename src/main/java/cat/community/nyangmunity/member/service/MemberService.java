package cat.community.nyangmunity.member.service;

import cat.community.nyangmunity.global.exception.AlreadyExistsNicknameException;
import cat.community.nyangmunity.global.exception.InvalidPasswordException;
import cat.community.nyangmunity.global.exception.MemberNotFoundException;
import cat.community.nyangmunity.global.provider.JwtTokenProvider;
import cat.community.nyangmunity.global.crypto.ScryptPasswordEncoder;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.member.editor.MemberEditor;
import cat.community.nyangmunity.global.exception.AlreadyExistsEmailException;
import cat.community.nyangmunity.global.exception.InvalidLoginInformationException;
import cat.community.nyangmunity.member.repository.MemberRepository;
import cat.community.nyangmunity.member.request.MemberEditForm;
import cat.community.nyangmunity.member.request.JoinRequest;
import cat.community.nyangmunity.member.request.LoginRequest;
import cat.community.nyangmunity.member.response.MemberInfos;
import cat.community.nyangmunity.member.response.MemberTokens;
import cat.community.nyangmunity.member.response.MemberLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ScryptPasswordEncoder scryptPasswordEncoder;

    @Transactional(readOnly = true)
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public MemberLoginResponse login(LoginRequest loginRequest) {
        Member member = findMemberByEmail(loginRequest.email()).orElseThrow(InvalidLoginInformationException::new);

        if(!isPasswordMatches(loginRequest.password(), member)) {
            throw new InvalidLoginInformationException();
        }

        return MemberLoginResponse.builder()
                .memberInfos(MemberInfos.from(member.getId(), member.getNickname()))
                .memberTokens(createTokens(member.getId()))
                .build();
    }

    private MemberTokens createTokens(Long memberId) {
        String accessToken = jwtTokenProvider.createAccessToken(memberId);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);
        tokenService.register(refreshToken, memberId);

        return MemberTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void join(JoinRequest joinRequest) {
        checkDuplicateEmail(joinRequest.email());
        checkDuplicateNickname(joinRequest.nickname());

        Member member = Member.builder()
                .email(joinRequest.email())
                .password(scryptPasswordEncoder.encrypt(joinRequest.password()))
                .nickname(joinRequest.nickname())
                .createDate(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public void checkDuplicateEmail(String email) {
        if (findMemberByEmail(email).isPresent()) throw new AlreadyExistsEmailException();
    }

    @Transactional(readOnly = true)
    public void checkDuplicateNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) throw new AlreadyExistsNicknameException();
    }

    @Transactional
    public void logout(Long memberId) {
        tokenService.deleteToken(memberId);
    }

    @Transactional
    public void edit(MemberEditForm memberEditForm, Long memberId) {
        Member member = findMemberById(memberId);

        if(!isPasswordMatches(memberEditForm.password(), member)) {
            throw new InvalidPasswordException();
        }

        MemberEditor.UserEditorBuilder memberEditorBuilder = member.toEditor();

        MemberEditor memberEditor = memberEditorBuilder
                .nickname(memberEditForm.nickname() != null && !memberEditForm.nickname().isEmpty()
                        ? memberEditForm.nickname() : member.getNickname())
                .password(memberEditForm.password() != null && !memberEditForm.password().isEmpty()
                        ? scryptPasswordEncoder.encrypt(memberEditForm.password()) : member.getPassword())
                .birthday(memberEditForm.birthday() != null && !memberEditForm.birthday().isEmpty() && !memberEditForm.birthday().equals("null")
                        ? LocalDate.parse(memberEditForm.birthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null)
                .build();

        member.edit(memberEditor);
    }

    @Transactional
    public void cancel(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    private boolean isPasswordMatches(String password, Member member) {
        return scryptPasswordEncoder.matches(password, member.getPassword());
    }
}

