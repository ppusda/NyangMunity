package cat.community.NyangMunity.service;

import cat.community.NyangMunity.controller.form.UserForm;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.repository.JoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class JoinService {

    private final JoinRepository joinRepository;

    public void register(UserForm userForm) {
        User user = User.builder()
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .nickname(userForm.getNickname())
                .birthday(LocalDate.parse(userForm.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .createDate(LocalDateTime.now())
                .build();

        joinRepository.save(user);
    }
}
