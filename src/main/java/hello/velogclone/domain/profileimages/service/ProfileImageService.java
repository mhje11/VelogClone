package hello.velogclone.domain.profileimages.service;

import hello.velogclone.domain.profileimages.entity.ProfileImage;
import hello.velogclone.domain.profileimages.repository.ProfileImageRepository;
import hello.velogclone.domain.user.dto.UserDto;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import hello.velogclone.global.exception.ProfileImageNotFoundException;
import hello.velogclone.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileImageService {
    private final ProfileImageRepository profileImageRepository;
    private final UserRepository userRepository;

    public void uploadProfileImage(UserDto userDto, MultipartFile file) throws IOException {

//        if (userDto.getProfileImageUrl() != null) {
//            Optional<ProfileImage> url = profileImageRepository.findByUrl(userDto.getProfileImageUrl());
//            User user = userRepository.findByLoginId(userDto.getLoginId()).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
//            profileImageRepository.delete(url.get());
//            String imagePathString = "src/main/resources/static" + user.getProfileImage().getUrl();
//            Path existingImagePath = Paths.get(imagePathString);
//            Files.deleteIfExists(existingImagePath);
//        }
        String cleanedFileName = cleanFileName(userDto.getLoginId() + "_" + file.getOriginalFilename());
        String imagePathString = "src/main/resources/static/images/profiles" + File.separator + cleanedFileName;
        Path imagePath = Paths.get(imagePathString);

        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, file.getBytes());
        log.info("File written to: " + imagePath.toString());

        User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        ProfileImage profileImage = new ProfileImage(file.getContentType(), "/images/profiles/" + cleanedFileName, user);

        userDto.setProfileImageUrl(profileImage.getUrl());
        profileImageRepository.save(profileImage);

        user.setProfileImage(profileImage);
        userRepository.save(user);
        log.info("Profile image updated for user: " + user.getLoginId());
    }

    @Transactional
    public void deleteExistingProfileImage(UserDto userDto) {
        User user = userRepository.findByLoginId(userDto.getLoginId()).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        ProfileImage existingImage = user.getProfileImage();
        if (existingImage != null) {
            try {
                String imagePathString = "src/main/resources/static" + existingImage.getUrl();
                Path existingImagePath = Paths.get(imagePathString);
                Files.deleteIfExists(existingImagePath);
                log.info("Deleted existing image file: " + imagePathString);
                profileImageRepository.delete(existingImage);
                user.setProfileImage(null);
                userRepository.save(user);
                log.info("Deleted existing image record from DB");
            } catch (IOException e) {
                log.error("Failed to delete existing image file; {}", e.getMessage());
            }
        } else {
            log.warn("Existing image not found for URL: " + userDto.getProfileImageUrl());
        }
    }
    @Transactional(readOnly = true)
    public ProfileImage findProfileImageByUrl(String url) {
        return profileImageRepository.findByUrl(url).orElseThrow(() -> new ProfileImageNotFoundException("이미지를 찾을 수 없습니다."));
    }


    // 파일 이름에 특수 문자 있을 시 처리
    private String cleanFileName(String fileName) {
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9._-]");
        return pattern.matcher(normalized).replaceAll("");
    }
}
