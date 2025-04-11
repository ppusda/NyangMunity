package cat.community.nyangmunity.member.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.global.crypto.ScryptPasswordEncoder;
import cat.community.nyangmunity.global.exception.member.AlreadyExistsMemberException;
import cat.community.nyangmunity.global.exception.member.InvalidLoginRequestException;
import cat.community.nyangmunity.global.exception.member.InvalidPasswordException;
import cat.community.nyangmunity.global.exception.member.MemberNotFoundException;
import cat.community.nyangmunity.member.editor.MemberEditor;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.member.repository.MemberRepository;
import cat.community.nyangmunity.member.request.JoinRequest;
import cat.community.nyangmunity.member.request.LoginRequest;
import cat.community.nyangmunity.member.request.MemberEditForm;
import cat.community.nyangmunity.member.response.MemberInfoResponse;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenFacadeService tokenFacadeService;
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
    public MemberAuthenticationResponse login(LoginRequest loginRequest) {
        Member member = findMemberByEmail(loginRequest.email()).orElseThrow(InvalidLoginRequestException::new);

        if(!isPasswordMatches(loginRequest.password(), member)) {
            throw new InvalidLoginRequestException();
        }

        return MemberAuthenticationResponse.builder()
                .memberInfoResponse(MemberInfoResponse.from(member))
                .memberTokens(tokenFacadeService.createTokens(member.getId()))
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
        if (findMemberByEmail(email).isPresent()) throw new AlreadyExistsMemberException();
    }

    @Transactional(readOnly = true)
    public void checkDuplicateNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) throw new AlreadyExistsMemberException();
    }

    @Transactional
    public void logout(Long memberId) {
        tokenFacadeService.deleteToken(memberId);
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

