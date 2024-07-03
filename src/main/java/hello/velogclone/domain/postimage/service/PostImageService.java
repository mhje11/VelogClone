package hello.velogclone.domain.postimage.service;

import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.postimage.entity.PostImage;
import hello.velogclone.domain.postimage.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostImageService {
    private final PostImageRepository postImageRepository;

    public String uploadImage(MultipartFile file, String userLoginId) throws IOException {
        String beforeFileName = userLoginId + "_" + file.getOriginalFilename();
        String fileName = cleanFileName(beforeFileName);
        String baseDir = "src/main/resources/static/images/posts/";
        Path imagePath = Paths.get(baseDir, fileName);

        if (!Files.exists(imagePath.getParent())) {
            Files.createDirectories(imagePath.getParent());
        }
        Files.write(imagePath, file.getBytes());

        // 이미지 크기 임의로 조정
        Thumbnails.of(imagePath.toFile())
                .size(300, 300)
                .toFile(imagePath.toFile());

        return "/images/posts/" + fileName;
    }

    public void deleteImagesByPostId(Long postId) {
        List<PostImage> postImages = postImageRepository.findAllByPostId(postId);
        String baseDir = "src/main/resources/static/images/posts/";

        for (PostImage postImage : postImages) {
            Path imagePath = Paths.get(baseDir, postImage.getUrl().replace("/images/posts/", ""));
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                log.info("이미지 파일 삭제 중 오류 발생: {}", postImage.getUrl(), e);
            }
        }
    }
    public void save(PostImage postImage) {
        postImageRepository.save(postImage);
    }

    private String cleanFileName(String fileName) {
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9._-]");
        return pattern.matcher(normalized).replaceAll("");
    }
}
