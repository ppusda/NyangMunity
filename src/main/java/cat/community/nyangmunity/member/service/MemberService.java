package cat.community.nyangmunity.member.service;

import cat.community.nyangmunity.global.exception.AlreadyExistsNicknameException;
import cat.community.nyangmunity.global.exception.InvalidPasswordException;
import cat.community.nyangmunity.global.exception.UserNotFoundException;
import cat.community.nyangmunity.global.provider.JwtTokenProvider;
import cat.community.nyangmunity.global.crypto.ScryptPasswordEncoder;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.token.service.TokenService;
import cat.community.nyangmunity.member.editor.MemberEditor;
import cat.community.nyangmunity.global.exception.AlreadyExistsEmailException;
import cat.community.nyangmunity.global.exception.InvalidLoginInformationException;
import cat.community.nyangmunity.member.repository.MemberRepository;
import cat.community.nyangmunity.member.request.MemberEditForm;
import cat.community.nyangmunity.member.request.MemberJoinForm;
import cat.community.nyangmunity.member.request.MemberLoginForm;
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
    public Member getUserById(Long userId) {
        return memberRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getUserByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public MemberLoginResponse userLogin(MemberLoginForm memberLoginForm) {
        Member member = getUserByEmail(memberLoginForm.email())
                .orElseThrow(InvalidLoginInformationException::new);

        if(!isPasswordMatches(memberLoginForm.password(), member)) {
            throw new InvalidLoginInformationException();
        }

        return MemberLoginResponse.builder()
                .memberInfos(MemberInfos.from(member.getId(), member.getNickname()))
                .memberTokens(createTokens(member.getId()))
                .build();
    }

    private MemberTokens createTokens(Long userId) {
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        tokenService.register(refreshToken, userId);

        return MemberTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void register(MemberJoinForm memberJoinForm) {
        checkDuplicateEmail(memberJoinForm.email());
        checkDuplicateNickname(memberJoinForm.nickname());

        Member member = Member.builder()
                .email(memberJoinForm.email())
                .password(scryptPasswordEncoder.encrypt(memberJoinForm.password()))
                .nickname(memberJoinForm.nickname())
                .createDate(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public void checkDuplicateEmail(String email) {
        getUserByEmail(email).orElseThrow(AlreadyExistsEmailException::new);
    }

    @Transactional(readOnly = true)
    public void checkDuplicateNickname(String nickname) {
        memberRepository.findByNickname(nickname).orElseThrow(AlreadyExistsNicknameException::new);
    }

    @Transactional
    public void userLogout(Long userId) {
        tokenService.deleteToken(userId);
    }

    @Transactional
    public void userEdit(MemberEditForm memberEditForm, Long userId) {
        Member member = getUserById(userId);

        if(!isPasswordMatches(memberEditForm.password(), member)) {
            throw new InvalidPasswordException();
        }

        MemberEditor.UserEditorBuilder userEditorBuilder = member.toEditor();

        MemberEditor memberEditor = userEditorBuilder
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
    public void userCancel(Long userId) {
        memberRepository.deleteById(userId);
    }

    private boolean isPasswordMatches(String password, Member member) {
        return scryptPasswordEncoder.matches(password, member.getPassword());
    }
}

