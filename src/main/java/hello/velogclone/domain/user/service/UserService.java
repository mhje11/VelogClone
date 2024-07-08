package hello.velogclone.domain.user.service;
import hello.velogclone.domain.follow.service.FollowService;
import hello.velogclone.domain.profileimages.service.ProfileImageService;
import hello.velogclone.domain.user.dto.UserDto;
import hello.velogclone.domain.user.entity.Role;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import hello.velogclone.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageService profileImageService;
    private final FollowService followService;


    public void signUp(UserDto userDto) {
        User user = convertToEntity(userDto);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));
        return UserDto.of(user);
    }

    public UserDto findUserByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));
        return UserDto.of(user);
    }

    public User findUserEntityByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
    }

    public User findUserByLoginIdOrElseNull(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElse(null);
        return user;
    }

    public void updateUser(UserDto userDto, String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

        user.setLoginId(userDto.getLoginId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setReceiveEmail(userDto.getReceiveEmail());
        user.setCommentNotification(userDto.getCommentNotification());
        userRepository.save(user);
    }
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    public User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setLoginId(userDto.getLoginId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRole(convertRole(userDto.getRole()));
        user.setReceiveEmail(userDto.getReceiveEmail());
        user.setCommentNotification(userDto.getCommentNotification());
        return user;
    }
    private Role convertRole(String role) {
        switch (role.toUpperCase()) {
            case "USER":
            case "ROLE_USER":
                return Role.ROLE_USER;
            case "ADMIN":
            case "ROLE_ADMIN":
                return Role.ROLE_ADMIN;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    public void uploadProfileImage(UserDto userDto, MultipartFile file) throws IOException {
        profileImageService.uploadProfileImage(userDto, file);
    }

    public void deleteProfileImage(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        profileImageService.deleteExistingProfileImage(UserDto.of(user));
    }

    public void deleteUser(User user) {
        followService.deleteByUser(user);
        userRepository.delete(user);
    }
}
