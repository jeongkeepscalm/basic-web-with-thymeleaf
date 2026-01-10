package dev.study.basicweb.user;

import dev.study.basicweb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender; // 이메일 전송 객체 주입

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    // EMAIL
    // 1. 비밀번호 변경 로직
    public void modifyPassword(SiteUser siteUser, String password) {
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
    }

    // 2. 임시 비밀번호 생성 및 이메일 전송 로직
    public void sendTemporaryPassword(String email) {
        Optional<SiteUser> siteUser = this.userRepository.findByEmail(email);

        if (siteUser.isPresent()) {
            SiteUser user = siteUser.get();

            // 임시 비밀번호 생성 (UUID 앞 10자리 사용)
            String tempPassword = UUID.randomUUID().toString().substring(0, 10);

            // 사용자 비밀번호를 임시 비밀번호로 변경 (암호화)
            user.setPassword(passwordEncoder.encode(tempPassword));
            this.userRepository.save(user);

            // 이메일 전송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("임시 비밀번호 안내 이메일입니다.");
            message.setText("회원님의 임시 비밀번호는 " + tempPassword + " 입니다. 로그인 후 비밀번호를 변경해주세요.");

            javaMailSender.send(message);
        } else {
            throw new DataNotFoundException("입력하신 이메일의 회원을 찾을 수 없습니다.");
        }
    }

    // 이메일로 회원 조회 (존재 여부 확인용)
    public boolean isEmailExist(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

}
