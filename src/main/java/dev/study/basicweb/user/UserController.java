package dev.study.basicweb.user;

import dev.study.basicweb.DataNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        /**
         * bindingResult.rejectValue : 필드 에러
         * bindingResult.reject : 폼 전체 에러
         */
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException dive) {
            dive.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    // ---------------- [비밀번호 찾기 (임시 비번 발송)] ----------------
    @GetMapping("/findpassword")
    public String findPassword() {
        return "find_password";
    }

    @PostMapping("/findpassword")
    public String findPassword(@RequestParam("email") String email, Model model) {
        try {
            this.userService.sendTemporaryPassword(email);
            model.addAttribute("message", "이메일로 임시 비밀번호가 발송되었습니다. 로그인 후 비밀번호를 변경해주세요.");
            return "login_form"; // 성공 시 로그인 페이지로 이동 (메시지 포함)
        } catch (DataNotFoundException e) {
            model.addAttribute("error", "해당 이메일로 등록된 계정이 없습니다.");
            return "find_password";
        } catch (Exception e) {
            model.addAttribute("error", "이메일 발송 중 오류가 발생했습니다.");
            return "find_password";
        }
    }

    // ---------------- [비밀번호 변경] ----------------
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/changepassword")
    public String changePassword(PasswordChangeForm passwordChangeForm) {
        return "password_change_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/changepassword")
    public String changePassword(@Valid PasswordChangeForm passwordChangeForm,
                                 BindingResult bindingResult,
                                 Principal principal) {
        if (bindingResult.hasErrors()) {
            return "password_change_form";
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());

        // 1. 현재 비밀번호 확인
        if (!passwordEncoder.matches(passwordChangeForm.getOldPassword(), siteUser.getPassword())) {
            bindingResult.rejectValue("oldPassword", "passwordInCorrect",
                    "현재 비밀번호가 일치하지 않습니다.");
            return "password_change_form";
        }

        // 2. 새 비밀번호와 확인 비밀번호 일치 여부
        if (!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getNewPassword2())) {
            bindingResult.rejectValue("newPassword2", "passwordInCorrect",
                    "새 비밀번호가 일치하지 않습니다.");
            return "password_change_form";
        }

        // 3. 비밀번호 변경 수행
        this.userService.modifyPassword(siteUser, passwordChangeForm.getNewPassword());

        return "redirect:/";
    }
}
