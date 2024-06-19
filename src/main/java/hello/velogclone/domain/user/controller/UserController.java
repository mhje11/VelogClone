package hello.velogclone.domain.user.controller;

import hello.velogclone.domain.user.dto.UserDto;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.service.UserService;
import hello.velogclone.global.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/api/signup")
    public String toSignUp(Model model) {
        model.addAttribute("user", new UserDto());
        return "user/signUpForm";
    }

    @PostMapping("/api/signup")
    public String signUp(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        try {
            userService.signUp(userDto);
            redirectAttributes.addFlashAttribute("message", "성공적으로 회원가입 됐습니다.");
            return "redirect:/api/login";
        } catch (Exception e) {
            log.error("오류 : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "redirect:/api/signup";
        }
    }

    @GetMapping("/api/login")
    public String toLogin(Model model) {
        model.addAttribute("user", new User());
        return "user/loginForm";
    }

    @PostMapping("/api/login")
    public void login(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
            // 사용자 인증 처리
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getLoginId());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //로그인후 리다이렉트할 url security config 에서 처리
    }


}
