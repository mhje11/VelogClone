package hello.velogclone.domain.user.service;

import hello.velogclone.domain.profileimages.entity.ProfileImage;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


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

    public void uploadProfileImage(String loginId, MultipartFile file) throws IOException {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        String uuid = UUID.randomUUID().toString();
        String fileName = user.getLoginId() + "_" + file.getOriginalFilename();
        String profileImageDirectory = "src/main/resources/static/images/profiles";
        String imagePathString = profileImageDirectory + File.separator + fileName;
        Path imagePath = Paths.get(imagePathString);

        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, file.getBytes());

        ProfileImage profileImage = new ProfileImage(file.getContentType(), "/images/profiles" + fileName, uuid, user);
        user.setProfileImage(profileImage);
        userRepository.save(user);
    }


    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setLoginId(userDto.getLoginId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRole(Role.valueOf(userDto.getRole()));
        user.setReceiveEmail(userDto.getReceiveEmail());
        user.setCommentNotification(userDto.getCommentNotification());
        return user;
    }
}
