package hello.velogclone.domain.user.controller;

import hello.velogclone.domain.profileimages.service.ProfileImageService;
import hello.velogclone.domain.user.dto.UserDto;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.service.UserService;
import hello.velogclone.global.exception.UserNotFoundException;
import hello.velogclone.global.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ProfileImageService profileImageService;

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
            redirectAttributes.addFlashAttribute("error", "이미 존재하는 아이디나 이메일 입니다.");
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
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getLoginId());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    @GetMapping("/api/profile/{loginId}")
    public String editProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model, @PathVariable("loginId") String loginId, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "로그인 후 이용 가능합니다.");
            return "redirect:/api/login";
        }

            if (!userDetails.getUsername().equals(loginId)) {
                redirectAttributes.addFlashAttribute("error", "수정할 권한이 없습니다.");
                return "redirect:/";
            }
            UserDto userDto = userService.findUserByLoginId(userDetails.getUsername());
            model.addAttribute("userDto", userDto);
            return "user/editprofile";
    }

    @PostMapping("/api/profile/{loginId}")
    public String editProfile(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal UserDetails userDetails, @PathVariable("loginId") String loginId,
                              @RequestParam("profileImage") MultipartFile profileImage) {
        try {
            UserDto currentUser = userService.findUserByLoginId(userDetails.getUsername());
            User userWithEmail = userService.findUserByEmail(userDto.getEmail());
            if (userWithEmail != null && !userWithEmail.getLoginId().equals(currentUser.getLoginId())) {
                redirectAttributes.addFlashAttribute("message", "해당 이메일이 이미 존재합니다.");
                return "redirect:/api/profile/" + loginId;
            }
            User userWithLoginId = userService.findUserByLoginIdOrElseNull(userDto.getLoginId());
            if (userWithLoginId != null && !userWithLoginId.getLoginId().equals(currentUser.getLoginId())) {
                redirectAttributes.addFlashAttribute("message", "해당 아이디가 이미 존재합니다.");
                return "redirect:/api/profile/" + loginId;
            }

            userService.updateUser(userDto, loginId);

            // SecurityContext 업데이트해주기
            UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(userDto.getLoginId());
            Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUserDetails, updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (!profileImage.isEmpty()) {
                try {
                    profileImageService.uploadProfileImage(userDto, profileImage);
                } catch (IOException e) {
                    log.info("파일 업로드 오류{}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "프로필 이미지 업로드 중 오류 발생");
                    return "redirect:/api/profile/" + loginId;
                }
            }

            redirectAttributes.addFlashAttribute("message", "수정이 성공적으로 됐습니다.");
            return "redirect:/";
        } catch (Exception e) {
            log.error("프로필 수정 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("message", "프로필 수정 중 오류가 발생했습니다.");
            return "redirect:/api/profile/" + loginId;
        }
    }

    @PostMapping("/api/profile/{loginId}/deleteProfileImage")
    public String deleteProfileImage(@PathVariable("loginId") String loginId, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (!userDetails.getUsername().equals(loginId)) {
                redirectAttributes.addFlashAttribute("error", "이미지를 삭제할 권한이 없습니다.");
                return "redirect:/api/profile/" + loginId;
            }
            userService.deleteProfileImage(loginId);
            redirectAttributes.addFlashAttribute("message", "프로필 이미지가 삭제되었습니다.");
            return "redirect:/api/profile/" + loginId;
        } catch (Exception e) {
            log.error("프로필 이미지 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "프로필 이미지 삭제 중 오류가 발생했습니다.");
            return "redirect:/api/profile/" + loginId;
        }
    }
}


